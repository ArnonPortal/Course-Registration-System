package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.rci.Command;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSEncoderDecoder implements MessageEncoderDecoder<Serializable> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short opcode = 0;
    private Account currAccount; //user who is currently logged in
    private String inputUsername = null; //username i received in a massage (not necessarily)
    private String inputPassword = null; //password i received in a massage (not necessarily)
    private Short inputCourseNum = null; //course number i received in a massage (not necessarily)
    private int zeroCount = 0; //counter for \0 char
    private Database databaseInstance = Database.getInstance();

    //Decodes byte by byte the request from the client and returns the Serializable response to the protocol.
    @Override
    public Serializable decodeNextByte(byte nextByte) {
        //reset for a new message
        if (len == 0) {
            zeroCount = 0;
            opcode = 0;
        }
        //2 strings cases
        if (opcode >= 1 & opcode <= 3 & nextByte == '\0') {
            zeroCount++;
            if (zeroCount == 1) {
                inputUsername = popString(2, len);
                return null;
            } else if (zeroCount == 2) {
                inputPassword = popString(2 + inputUsername.length(), len);
                return popCommand();
            }
        }

        //course number cases
        if (opcode >= 5 & opcode <= 10 & opcode != 8 & len == 3) {
            pushByte(nextByte); //we dont have the char \0 so we want to push the last byte here
            inputCourseNum = bytesToShort(Arrays.copyOfRange(bytes, 2, 4));
            return popCommand();
        }

        //1 string case
        if (opcode == 8 & nextByte == '\0') {
            inputUsername = popString(2, len);
            return popCommand();
        }

        pushByte(nextByte);
        if (len == 2) {
            opcode = bytesToShort(bytes);
        }

        //only opcode messages cases
        if (opcode == 4 | opcode == 11) {
            return popCommand();
        }
        return null;
    }

    //string decoder
    private String popString(int start, int end) {
        return new String(bytes, start, end - start, StandardCharsets.UTF_8);
    }


    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }
    //After decoding is finished , sending to the protocol the clients request according to the opcode given.
    private Command<Database> popCommand() {
        //Reset for next decoding
        len = 0;
        switch (opcode) {
            case 1:
                return opcode1();
            case 2:
                return opcode2();
            case 3:
                return opcode3();
            case 4:
                return opcode4();
            case 5:
                return opcode5();
            case 6:
                return opcode6();
            case 7:
                return opcode7();
            case 8:
                return opcode8();
            case 9:
                return opcode9();
            case 10:
                return opcode10();
            case 11:
                return opcode11();
        }
        return null;
    }

    private Command<Database> opcode1() {
        return (Database database) -> {
            if (!isCurrAccountLoggedIn() && registerValidation() &&
                    database.signUpAdmin(new Admin(inputUsername, inputPassword))) {
                resetInput();
                return (short) 12 + "" + (short) 1 + ": Successfully Signed Up!" + "\0";
            }
            resetInput();
            return (short) 13 + "" + (short) 1;
        };
    }

    private Command<Database> opcode2() {
        return (Database database) -> {
            if (!isCurrAccountLoggedIn() && registerValidation() &&
                    database.signUpStudent(new Student(inputUsername, inputPassword))) {
                resetInput();
                return (short) 12 + "" + (short) 2 + ": Successfully Signed Up!" + "\0";
            }
            resetInput();
            return (short) 13 + "" + (short) 2;
        };
    }

    private Command<Database> opcode3() {
        return (Database database) -> {
            if (database.getAccount(inputUsername) == null ||
                    !database.loginValidation(inputUsername, inputPassword) ||
                    isCurrAccountLoggedIn() || isInputLoggedIn()) {
                return (short) 13 + "" + (short) 3;
            }
            login();
            return (short) 12 + "" + (short) 3 + ": Successfully Logged In!" + "\0";
        };
    }

    private Command<Database> opcode4() {
        return (Database database) -> {
            if (!isCurrAccountLoggedIn()) {
                return (short) 13 + "" + (short) 4;
            }
            logout();
            return (short) 12 + "" + (short) 4 + ": Successfully Logged Out!" + "\0";
        };
    }

    private Command<Database> opcode5() {
        return (Database database) -> {
            if (!studentCheck() ||
                    !database.registerCourse(currAccount.getUsername(), inputCourseNum)) {
                return (short) 13 + "" + (short) 5;
            }
            return (short) 12 + "" + (short) 5 + ": Successfully Registered!" + "\0";
        };
    }

    private Command<Database> opcode6() {
        return (Database database) -> {
            if (!studentCheck() || !database.courseExist(inputCourseNum)) {
                return (short) 13 + "" + (short) 6;
            }
            String kdamCourses = "";
            for (Integer i : database.getCourse(inputCourseNum).getKdamCourses()) {
                kdamCourses += i + ",";
            }
            if(kdamCourses.length() != 0) {
                kdamCourses ="[" + kdamCourses.substring(0, kdamCourses.length() - 1) + "]";
            }
            else{
                kdamCourses = "[]";
            }
            return (short) 12 + "" + (short) 6 + kdamCourses + "\0";
        };
    }

    private Command<Database> opcode7() {
        return (Database database) -> {
            if (!adminCheck() || !database.courseExist(inputCourseNum)) {
                return (short) 13 + "" + (short) 7;
            }
            return (short) 12 + "" + (short) 7 + "\n" + database.getCourseStatus(inputCourseNum) + "\0";
        };
    }

    private Command<Database> opcode8() {
        return (Database database) -> {
            if (!adminCheck() || database.getAccount(inputUsername) == null ||
                    database.isAdmin(database.getAccount(inputUsername))) {
                return (short) 13 + "" + (short) 8;
            }
            return (short) 12 + "" + (short) 8 + "\n" + database.getStudentStatus(inputUsername) + "\0";
        };
    }

    private Command<Database> opcode9() {
        return (Database database) -> {
            if (!studentCheck() || !database.courseExist(inputCourseNum)) {
                return (short) 13 + "" + (short) 9;
            }
            if (((Student) currAccount).isRegisterdCourse(inputCourseNum)) {
                return (short) 12 + "" + (short) 9 + "REGISTERED" + "\0";
            }
            return (short) 12 + "" + (short) 9 + "NOT REGISTERED" + "\0";
        };
    }

    private Command<Database> opcode10() {
        return (Database database) -> {
            if (!studentCheck() || !database.courseExist(inputCourseNum) ||
                    !database.unregisterCourse(currAccount.getUsername(), inputCourseNum)) {
                return (short) 13 + "" + (short) 10;
            }
            return (short) 12 + "" + (short) 10;
        };
    }

    private Command<Database> opcode11() {
        return (Database database) -> {
            if (!studentCheck()) {
                return (short) 13 + "" + (short) 11;
            }
            return (short) 12 + "" + (short) 11 + ((Student) currAccount).getMyCourses().toString().replace(" ", "") + "\0";
        };
    }

    //
    private boolean isCurrAccountLoggedIn() {
        return currAccount != null && currAccount.isLoggedIn();
    }

    //in order to validate that the wanted account isn't already registered on different client
    private boolean isInputLoggedIn(){
        return databaseInstance.getAccount(inputUsername) != null &&
                databaseInstance.getAccount(inputUsername).isLoggedIn();
    }

    private void login() {
        currAccount = databaseInstance.getAccount(inputUsername);
        currAccount.login();
    }

    private void logout() {
        currAccount.logout();
        currAccount = null;
    }

    //validations for admin actions
    private boolean adminCheck() {
        return currAccount != null && isCurrAccountLoggedIn() & databaseInstance.isAdmin(currAccount);
    }

    //validations for student actions
    private boolean studentCheck() {
        return currAccount != null && isCurrAccountLoggedIn() & !databaseInstance.isAdmin(currAccount);
    }

    //validate the input for registration
    public boolean registerValidation(){
        return inputUsername != null & inputPassword != null;
    }

    private void resetInput(){
        inputUsername = null;
        inputPassword = null;
    }
    
    //Encoding the response given from the protocol into an array of bytes and sends it back to the client.
    @Override
    public byte[] encode(Serializable message) {
        byte[] output;
        byte[] stringBytes;
        boolean isAck = false;
        if (message.toString().startsWith("12"))
            isAck = true;

        //check for output length
        if (opcode > 9) {
            output = new byte[4 + message.toString().substring(4).length()];
            stringBytes = message.toString().substring(4).getBytes();
        } else {
            output = new byte[4 + message.toString().substring(3).length()];
            stringBytes = message.toString().substring(3).getBytes();
        }

        //encoding ack/error opcode
        byte[] opCodeBytes;
        if (isAck) {
            opCodeBytes = shortToBytes((short) 12);
        } else {
            opCodeBytes = shortToBytes((short) 13);
        }

        //encoding message opcode
        byte[] messageOpCodeBytes = shortToBytes(opcode);
        
        //Concatinating the results
        ByteBuffer buff = ByteBuffer.wrap(output);
        buff.put(opCodeBytes);
        buff.put(messageOpCodeBytes);
        buff.put(stringBytes);
        
        return buff.array();

    }

}
