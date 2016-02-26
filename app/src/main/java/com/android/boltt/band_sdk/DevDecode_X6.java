package com.android.boltt.band_sdk;

import java.io.PrintStream;

public class DevDecode_X6 {
    public String[] decode_MAC_SN(byte[] data) {
        if (data.length < 5) {
            return null;
        }
        if (data[0] != -91) {
            return null;
        }
        int dataLength = data.length - 4;
        if (data[1] != dataLength) {
            return null;
        }
        byte[] crc = this.CRC_16(data, 2, dataLength);
        if (crc.length != 2) {
            return null;
        }
        if (crc[0] == data[data.length - 1] && crc[1] == data[data.length - 2]) {
            byte[] devAddress = DevDecode_X6.cutBytes(data, 3, 6);
            devAddress = DevDecode_X6.switchBytes(devAddress);
            byte[] devSN = DevDecode_X6.cutBytes(data, 9, 4);
            devSN = DevDecode_X6.switchBytes(devSN);
            return new String[]{DevDecode_X6.byteToString(devAddress), DevDecode_X6.byteToString(devSN)};
        }
        return null;
    }

    public int[] decode_PersonalInfo(byte[] data, int type) {
        if (data.length < 5) {
            return null;
        }
        if (data[0] != -91) {
            return null;
        }
        int dataLength = data.length - 4;
        if (data[1] != dataLength) {
            return null;
        }
        byte[] crc = this.CRC_16(data, 2, dataLength);
        if (crc.length != 2) {
            return null;
        }
        if (crc[0] == data[data.length - 1] && crc[1] == data[data.length - 2]) {
            if (type == 1) {
                int height = DevDecode_X6.cutBytes(data, 4, 1)[0] & 255;
                int weight = DevDecode_X6.cutBytes(data, 5, 1)[0] & 255;
                int sex = DevDecode_X6.cutBytes(data, 6, 1)[0] & 255;
                int age = DevDecode_X6.cutBytes(data, 7, 1)[0] & 255;
                return new int[]{height, weight, sex, age};
            }
            if (type == 2) {
                data = DevDecode_X6.switchBytes(data);
                byte[] stand_bytes = DevDecode_X6.cutBytes(data, 2, 2);
                int stand = DevDecode_X6.bytesToInt2_2Bytes(stand_bytes);
                return new int[]{stand};
            }
            if (type == 3) {
                data = DevDecode_X6.switchBytes(data);
                byte[] target_bytes = DevDecode_X6.cutBytes(data, 2, 4);
                int target = DevDecode_X6.byteToInt2_4Bytes(target_bytes);
                return new int[]{target};
            }
            if (type == 4) {
                int start_Hour = DevDecode_X6.cutBytes(data, 4, 1)[0];
                int start_Minute = DevDecode_X6.cutBytes(data, 5, 1)[0];
                int end_Hour = DevDecode_X6.cutBytes(data, 6, 1)[0];
                int end_Minute = DevDecode_X6.cutBytes(data, 7, 1)[0];
                return new int[]{start_Hour, start_Minute, end_Hour, end_Minute};
            }
            if (type == 5) {
                int disconnectNotify = DevDecode_X6.cutBytes(data, 4, 1)[0];
                int timeType = DevDecode_X6.cutBytes(data, 5, 1)[0];
                int UIType = DevDecode_X6.cutBytes(data, 6, 1)[0];
                return new int[]{disconnectNotify, timeType, UIType};
            }
            if (type == 6) {
                int enable = DevDecode_X6.cutBytes(data, 4, 1)[0];
                int start_Hour = DevDecode_X6.cutBytes(data, 5, 1)[0];
                int start_Minute = DevDecode_X6.cutBytes(data, 6, 1)[0];
                int end_Hour = DevDecode_X6.cutBytes(data, 7, 1)[0];
                int end_Minute = DevDecode_X6.cutBytes(data, 8, 1)[0];
                return new int[]{enable, start_Hour, start_Minute, end_Hour, end_Minute};
            }
            if (type == 7) {
                int temp = DevDecode_X6.cutBytes(data, 4, 1)[0];
                return new int[]{temp};
            }
            if (type == 8) {
                int temp = DevDecode_X6.cutBytes(data, 4, 1)[0];
                return new int[]{temp};
            }
            if (type == 9) {
                int temp = DevDecode_X6.cutBytes(data, 4, 1)[0];
                return new int[]{temp};
            }
            return null;
        }
        return null;
    }

    public int[] decode_Date_Time(byte[] data) {
        if (data.length < 5) {
            return null;
        }
        if (data[0] != -91) {
            return null;
        }
        int dataLength = data.length - 4;
        if (data[1] != dataLength) {
            return null;
        }
        byte[] crc = this.CRC_16(data, 2, dataLength);
        if (crc.length != 2) {
            return null;
        }
        if (crc[0] == data[data.length - 1] && crc[1] == data[data.length - 2]) {
            byte year01 = DevDecode_X6.cutBytes(data, 3, 1)[0];
            byte year02 = DevDecode_X6.cutBytes(data, 4, 1)[0];
            int year = DevDecode_X6.bytesToInt2_2Bytes(new byte[]{year01, year02});
            int month = DevDecode_X6.cutBytes(data, 5, 1)[0];
            int day = DevDecode_X6.cutBytes(data, 6, 1)[0];
            int hour = DevDecode_X6.cutBytes(data, 7, 1)[0];
            int minute = DevDecode_X6.cutBytes(data, 8, 1)[0];
            int second = DevDecode_X6.cutBytes(data, 9, 1)[0];
            int week = DevDecode_X6.cutBytes(data, 10, 1)[0];
            return new int[]{year, month, day, hour, minute, second, week};
        }
        return null;
    }

    public int[] decode_AlarmClock(byte[] data) {
        if (data.length < 5) {
            return null;
        }
        if (data[0] != -91) {
            return null;
        }
        int dataLength = data.length - 4;
        if (data[1] != dataLength) {
            return null;
        }
        byte[] crc = this.CRC_16(data, 2, dataLength);
        if (crc.length != 2) {
            return null;
        }
        if (crc[0] == data[data.length - 1] && crc[1] == data[data.length - 2]) {
            int ID = DevDecode_X6.cutBytes(data, 3, 1)[0];
            int type = DevDecode_X6.cutBytes(data, 4, 1)[0];
            int enable = DevDecode_X6.cutBytes(data, 5, 1)[0];
            int hour = DevDecode_X6.cutBytes(data, 6, 1)[0];
            int minute = DevDecode_X6.cutBytes(data, 7, 1)[0];
            int remindTime = DevDecode_X6.cutBytes(data, 8, 1)[0];
            DevDecode_X6.weekTransTo(enable);
            return new int[]{ID, type, enable, hour, minute, remindTime};
        }
        return null;
    }

    public int decode_CurrentValue_Auto(byte[] data) {
        if (data == null) {
            return 0;
        }
        data = DevDecode_X6.switchBytes(data);
        int steps = DevDecode_X6.byteToInt2_4Bytes(data);
        return steps;
    }

    public int[] decode_CurrentValue(byte[] data) {
        if (data.length < 5) {
            return null;
        }
        if (data[0] != -91) {
            return null;
        }
        int dataLength = data.length - 4;
        if (data[1] != dataLength) {
            return null;
        }
        byte[] crc = this.CRC_16(data, 2, dataLength);
        if (crc.length != 2) {
            return null;
        }
        if (crc[0] == data[data.length - 1] && crc[1] == data[data.length - 2]) {
            int steps_int = 0;
            int calories_int = 0;
            int distances_int = 0;
            byte[] steps = DevDecode_X6.cutBytes(data, 4, 4);
            byte[] calories = DevDecode_X6.cutBytes(data, 8, 4);
            byte[] distances = DevDecode_X6.cutBytes(data, 12, 4);
            steps = DevDecode_X6.switchBytes(steps);
            calories = DevDecode_X6.switchBytes(calories);
            distances = DevDecode_X6.switchBytes(distances);
            steps_int = DevDecode_X6.byteToInt2_4Bytes(steps);
            calories_int = DevDecode_X6.byteToInt2_4Bytes(calories);
            distances_int = DevDecode_X6.byteToInt2_4Bytes(distances);
            return new int[]{steps_int, distances_int, calories_int};
        }
        return null;
    }

    public int[][] decode_HistoryRecodeDate(byte[] data, int length) {
        if (data.length < 5) {
            return null;
        }
        if (data[0] != -91) {
            return null;
        }
        int dataLength = length - 4;
        if (data[1] != dataLength) {
            return null;
        }
        byte[] crc = this.CRC_16(data, 2, dataLength);
        if (crc.length != 2) {
            return null;
        }
        if (crc[0] == data[length - 1] && crc[1] == data[length - 2]) {
            int[][] blockData = new int[7][4];
            byte[][] blockData_Byte = new byte[7][5];
            int i = 0;
            while (i * 5 + 8 + 1 < length) {
                int n = i * 5 + 3;
                blockData_Byte[i] = DevDecode_X6.cutBytes(data, n, 5);
                ++i;
            }
            i = 0;
            while (i < blockData_Byte.length) {
                byte temp = blockData_Byte[i][1];
                blockData_Byte[i][1] = blockData_Byte[i][2];
                blockData_Byte[i][1] = temp;
                blockData[i][0] = blockData_Byte[i][0];
                blockData[i][1] = DevDecode_X6.bytesToInt2_2Bytes(new byte[]{blockData_Byte[i][2], blockData_Byte[i][1]});
                blockData[i][2] = blockData_Byte[i][3];
                blockData[i][3] = blockData_Byte[i][4];
                ++i;
            }
            return blockData;
        }
        return null;
    }

    public int[][] decode_HistoryRecodeDatail(byte[] data) {
        if (data.length < 67) {
            return null;
        }
        if (data[0] != -91) {
            return null;
        }
        int dataLength = data.length - 4;
        if (data[1] != dataLength) {
            return null;
        }
        byte[] crc = this.CRC_16(data, 2, dataLength);
        if (crc.length != 2) {
            return null;
        }
        if (crc[0] == data[data.length - 1] && crc[1] == data[data.length - 2]) {
            int[][] steps = new int[31][2];
            steps[0][0] = DevDecode_X6.cutBytes(data, 4, 1)[0];
            int i = 1;
            while (i < steps.length) {
                byte[] stepsData = DevDecode_X6.cutBytes(data, (i + 1) * 2 + 1, 2);
                steps[i] = DevDecode_X6.separateData(stepsData);
                if (steps[i][1] == 4095) {
                    steps[i][0] = -1;
                    steps[i][1] = 0;
                } else if (steps[i][1] == 3840) {
                    steps[i][0] = -1;
                    steps[i][1] = 0;
                }
                ++i;
            }
            return steps;
        }
        return null;
    }

    public int[] getHistoryDistance(int[] historySteps, int userHeight) {
        if (historySteps == null) {
            return null;
        }
        int STRIDE_FACTOR = 415;
        int history_Counts = historySteps.length;
        int[] historyDistance = new int[history_Counts];
        try {
            int i = 0;
            while (i < history_Counts) {
                historyDistance[i] = historySteps[i] * userHeight / 241;
                ++i;
            }
            return historyDistance;
        }
        catch (Exception e) {
            System.out.println("\u5f02\u5e38");
            return null;
        }
    }

    public int[] getHistoryCalories(int[] historyDistan, int userWeight) {
        if (historyDistan == null) {
            return null;
        }
        int history_Counts = historyDistan.length;
        int[] historyCalories = new int[history_Counts];
        try {
            int i = 0;
            while (i < history_Counts) {
                historyCalories[i] = userWeight * historyDistan[i] / 965;
                ++i;
            }
            return historyCalories;
        }
        catch (Exception e) {
            System.out.println("\u5f02\u5e38");
            return null;
        }
    }

    public static int[] separateData(byte[] res) {
        int[] targets = new int[2];
        byte temp_Type = res[1];
        byte temp_Step = res[0];
        targets[0] = temp_Type >>> 4 & 15;
        res[1] = (byte)(res[1] & 15);
        targets[1] = DevDecode_X6.bytesToInt2_2Bytes(new byte[]{res[1], temp_Step});
        return targets;
    }

    public static int[] weekTransTo(int week) {
        try {
            int[] intweekclock = new int[8];
            byte temp = (byte)week;
            int j = 0;
            while (j < 8) {
                intweekclock[7 - j] = (temp & (int)Math.pow(2.0, 7 - j)) >>> 7 - j;
                ++j;
            }
            return intweekclock;
        }
        catch (Exception e) {
            return null;
        }
    }

    private byte[] CRC_16(byte[] data, int start, int length) {
        try {
            short crc_result = 0;
            int Poly = 4129;
            int i = start;
            while (i < start + length) {
                int j = 128;
                while (j != 0) {
                    if ((crc_result & 32768) != 0) {
                        crc_result = (short)(crc_result << 1);
                        crc_result = (short)(crc_result ^ Poly);
                    } else {
                        crc_result = (short)(crc_result << 1);
                    }
                    if ((data[i] & j) != 0) {
                        crc_result = (short)(crc_result ^ Poly);
                    }
                    j >>= 1;
                }
                ++i;
            }
            return this.short2bytes(crc_result);
        }
        catch (Exception crc_result) {
            return this.short2bytes(-1);
        }
    }

    private byte[] short2bytes(int j) {
        byte[] bytes = new byte[2];
        int i = 1;
        while (i >= 0) {
            bytes[i] = (byte)(j % 256);
            j = (short)(j >> 8);
            --i;
        }
        return bytes;
    }

    public static byte[] switchBytes(byte[] data) {
        try {
            int length = data.length;
            byte[] data_temp = new byte[length];
            int i = 0;
            while (i < length) {
                data_temp[i] = data[length - i - 1];
                ++i;
            }
            return data_temp;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static byte[] cutBytes(byte[] data, int start, int length) {
        byte[] data_temp = new byte[length];
        int i = 0;
        while (i < length) {
            data_temp[i] = data[start + i];
            ++i;
        }
        return data_temp;
    }

    public static int byteToInt2_4Bytes(byte[] b) {
        int mask = 255;
        int temp = 0;
        int n = 0;
        int i = 0;
        while (i < 4) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
            ++i;
        }
        return n & -1;
    }

    public static int bytesToInt2_2Bytes(byte[] src) {
        int value = (src[0] & 255) << 8 | src[1] & 255;
        return value;
    }

    public static String byteToString(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder(data.length);
        byte[] arrby = data;
        int n = arrby.length;
        int n2 = 0;
        while (n2 < n) {
            byte byteChar = arrby[n2];
            stringBuilder.append(String.format("%02X ", Byte.valueOf(byteChar)).toString());
            ++n2;
        }
        return stringBuilder.toString();
    }
}
