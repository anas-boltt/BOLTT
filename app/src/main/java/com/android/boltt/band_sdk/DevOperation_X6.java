package com.android.boltt.band_sdk;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Calendar;

public class DevOperation_X6 {
    private BluetoothGattCharacteristic cha_Operiation_NotificationData;
    private BluetoothGattCharacteristic characteristic_Write;
    private BluetoothGatt mBluetoothGatt;

    public DevOperation_X6(BluetoothGatt b) {
        this.mBluetoothGatt = b;
    }

    public void setWriteCharacteristic(BluetoothGattCharacteristic cha) {
        this.characteristic_Write = cha;
    }

    public void setWriteCharacteristic_NotificationData(BluetoothGattCharacteristic cha) {
        this.cha_Operiation_NotificationData = cha;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        try {
            this.mBluetoothGatt.readCharacteristic(characteristic);
        } catch (Exception e) {
        }
    }

    private void wirteCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
        try {
            characteristic.setValue(value);
            characteristic.setWriteType(1);
            this.mBluetoothGatt.writeCharacteristic(characteristic);
        } catch (Exception e) {
        }
    }

    public void readDevVision() {
        try {
            byte[] code_data = new byte[]{(byte) 2};
            byte[] crc = short2bytes(CRC_16(code_data));
            byte[] data_Send = new byte[(code_data.length + 4)];
            data_Send[0] = (byte) -91;
            data_Send[1] = (byte) code_data.length;
            data_Send[data_Send.length - 2] = crc[1];
            data_Send[data_Send.length - 1] = crc[0];
            for (int i = 2; i < code_data.length + 2; i++) {
                data_Send[i] = code_data[i - 2];
            }
            wirteCharacteristic(this.characteristic_Write, data_Send);
        } catch (Exception e) {
            System.out.println("\u8bfb\u53d6\u7248\u672c\u53f7\u5f02\u5e38" + e.toString());
        }
    }

    public void readMAC_SN() {
        try {
            writeCode(new byte[]{(byte) 2}, true);
        } catch (Exception e) {
            System.out.println("\u8bfb\u53d6MAC_SN\u53f7\u5f02\u5e38" + e.toString());
        }
    }

    public void readPersonalInfo(byte InfoID) {
        try {
            writeCode(new byte[]{(byte) 32, InfoID}, true);
        } catch (Exception e) {
            System.out.println("\u8bfb\u53d6\u4e2a\u4eba\u4fe1\u606f\u5f02\u5e38" + e.toString());
        }
    }

    public void writePersonalInfo(byte InfoID, byte[] data) {
        try {
            byte[] newData = new byte[(data.length + 2)];
            newData[0] = (byte) 32;
            newData[1] = InfoID;
            for (int i = 0; i < data.length; i++) {
                newData[i + 2] = data[i];
            }
            System.out.println("\u5199\u5165\u4e2a\u4eba\u4fe1\u606f" + byteToString(newData));
            writeCode(newData, false);
        } catch (Exception e) {
            System.out.println("\u5199\u5165\u4e2a\u4eba\u4fe1\u606f\u5f02\u5e38" + e.toString());
        }
    }

    public void readDate_Time() {
        try {
            writeCode(new byte[]{(byte) 33}, true);
        } catch (Exception e) {
            System.out.println(" \u8bfb\u53d6\u65f6\u95f4\u5f02\u5e38" + e.toString());
        }
    }

    public void writeDate_Time() {
        try {
            Calendar ca = Calendar.getInstance();
            ca.get(11);
            int year = ca.get(1);
            int month = ca.get(2) + 1;
            int day = ca.get(5);
            int hour = ca.get(10);
            int minute = ca.get(12);
            int second = ca.get(13);
            int ap = ca.get(9);
            int dayOfWeek = ca.get(7);
            if (dayOfWeek == 1) {
                dayOfWeek = 7;
            } else {
                dayOfWeek--;
            }
            if (ap == 1 && hour < 12) {
                hour += 12;
            }
            byte[] year01 = int2Bytes_2Bytes(year);
            writeCode(new byte[]{(byte) 33, year01[1], year01[0], (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) second, (byte) dayOfWeek}, false);
        } catch (Exception e) {
            System.out.println("\u5199\u5165\u65f6\u95f4\u5f02\u5e38" + e.toString());
        }
    }

    public void readAlarmClock(byte clockID) {
        try {
            writeCode(new byte[]{(byte) 34, clockID}, true);
        } catch (Exception e) {
            System.out.println("\u8bfb\u53d6\u95f9\u949f\u5f02\u5e38" + e.toString());
        }
    }

    public void writeAlarmClock(byte[] data) {
        if (data != null) {
            try {
                if (data.length == 6) {
                    byte[] alarmClockData = new byte[]{(byte) 34, data[0], data[1], data[2], data[3], data[4], data[5]};
                    System.out.println("----\u5199\u5165\u95f9\u949f  ID:" + data[0] + " type:" + data[1] + " enable:" + data[2] + " Time:" + data[3] + ":" + data[4] + " remindTime:" + data[5]);
                    writeCode(alarmClockData, false);
                }
            } catch (Exception e) {
                System.out.println("\u5199\u5165\u95f9\u949f\u5f02\u5e38" + e.toString());
            }
        }
    }

    public void readCurrentValue() {
        try {
            writeCode(new byte[]{(byte) 3, (byte) 1}, true);
        } catch (Exception e) {
            System.out.println("\u8bfb\u53d6\u5f53\u524d\u8fd0\u52a8\u6570\u636e\u5f02\u5e38" + e.toString());
        }
    }

    public void readHistoryRecodeDate() {
        try {
            writeCode(new byte[]{(byte) 4}, true);
        } catch (Exception e) {
            System.out.println("\u8bfb\u53d6\u5386\u53f2\u6620\u5c04\u8868\u5f02\u5e38" + e.toString());
        }
    }

    public void readHistoryRecodeDatail(byte blockID, byte hour) {
        writeCode(new byte[]{(byte) 5, blockID, hour}, true);
    }

    public void readHistoryRecodeStatistics() {
        writeCode(new byte[]{(byte) 6}, true);
    }

    public void writerAlert(byte alerType) {
        writeCode(new byte[]{(byte) 35, alerType}, false);
    }

    public void writerNotification(byte eventFlag, byte categoryID) {
        byte eventFlags = eventFlag;
        byte category_id = categoryID;
        writeCode(new byte[]{(byte) 97, (byte) 0, eventFlags, category_id, (byte) 1, (byte) -95, (byte) -94, (byte) -93, (byte) -92}, false);
    }

    public void writerNotificationCancel() {
        writeCode(new byte[]{(byte) 97, (byte) 2, (byte) 1, (byte) 1, (byte) 1, (byte) -95, (byte) -94, (byte) -93, (byte) -92}, false);
    }

    public void sendNotificationData(String title, String msgText, String packageName) {
        byte[] Attribute1;
        byte[] Attribute3;
        byte[] Attribute0;
        byte[] attributeData3;
        try {
            Attribute1 = title.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Attribute1 = new byte[0];
            e.printStackTrace();
        }
        try {
            Attribute3 = msgText.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e2) {
            Attribute3 = new byte[0];
            e2.printStackTrace();
        }
        try {
            Attribute0 = packageName.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e22) {
            Attribute0 = new byte[0];
            e22.printStackTrace();
        }
        byte[] attributeData1 = packageAttributeData((byte) 1, Attribute1, 32);
        if (msgText.equals("")) {
            attributeData3 = new byte[0];
        } else {
            attributeData3 = packageAttributeData((byte) 3, Attribute3, 160);
        }
        byte[][] allData = reCountData(20, packageNotificationData(attributeData1, attributeData3, packageAttributeData((byte) 0, Attribute0, 32)));
        for (byte[] value : allData) {
            try {
                this.cha_Operiation_NotificationData.setValue(value);
                this.cha_Operiation_NotificationData.setWriteType(1);
                this.mBluetoothGatt.writeCharacteristic(this.cha_Operiation_NotificationData);
                Thread.sleep(100);
            } catch (Exception e3) {
            }
        }
    }

    public byte[] packageAttributeData(byte attributeID, byte[] data, int length) {
        int dataLength = data.length;
        if (dataLength > length) {
            dataLength = length;
        }
        byte[] data_Length2Bytes = int2Bytes_2Bytes(dataLength);
        byte length1 = data_Length2Bytes[0];
        byte length2 = data_Length2Bytes[1];
        byte[] allData = new byte[(dataLength + 3)];
        allData[0] = attributeID;
        allData[1] = length2;
        allData[2] = length1;
        for (int i = 0; i < dataLength; i++) {
            allData[i + 3] = data[i];
        }
        return allData;
    }

    public byte[] packageNotificationData(byte[] Attribute1, byte[] Attribute3, byte[] Attribute0) {
        int i;
        int leng1 = Attribute1.length;
        int leng3 = Attribute3.length;
        byte[] allData = new byte[(((leng1 + 5) + leng3) + Attribute0.length)];
        allData[0] = (byte) 0;
        allData[1] = (byte) -95;
        allData[2] = (byte) -94;
        allData[3] = (byte) -93;
        allData[4] = (byte) -92;
        for (i = 0; i < Attribute1.length; i++) {
            allData[i + 5] = Attribute1[i];
        }
        for (i = 0; i < Attribute3.length; i++) {
            allData[(i + 5) + leng1] = Attribute3[i];
        }
        for (i = 0; i < Attribute0.length; i++) {
            allData[((i + 5) + leng1) + leng3] = Attribute0[i];
        }
        return allData;
    }

    public byte[][] reCountData(int reCount, byte[] data) {
        int dataLength = data.length;
        int count = dataLength / reCount;
        int lastCount = dataLength % reCount;
        if (lastCount != 0) {
            count++;
        }
        byte[][] dataTemp = (byte[][]) Array.newInstance(Byte.TYPE, new int[]{count, reCount});
        int i = 0;
        while (i < count) {
            byte[] temp;
            int j;
            if (i != count - 1 || lastCount == 0) {
                temp = new byte[reCount];
                for (j = 0; j < temp.length; j++) {
                    temp[j] = data[(i * reCount) + j];
                }
                dataTemp[i] = temp;
                i++;
            } else {
                temp = new byte[lastCount];
                for (j = 0; j < temp.length; j++) {
                    temp[j] = data[(i * reCount) + j];
                }
                dataTemp[i] = temp;
                return dataTemp;
            }
        }
        return dataTemp;
    }

    public void setReset(byte resetType) {
        writeCode(new byte[]{(byte) 64, resetType}, true);
    }

    public void writeCode(byte[] opCode_Data, boolean isRead) {
        byte[] code_data = opCode_Data;
        byte[] crc = short2bytes(CRC_16(code_data));
        byte[] data_Send = new byte[(code_data.length + 4)];
        if (isRead) {
            data_Send[0] = (byte) -91;
        } else {
            data_Send[0] = (byte) 37;
        }
        data_Send[1] = (byte) code_data.length;
        data_Send[data_Send.length - 2] = crc[1];
        data_Send[data_Send.length - 1] = crc[0];
        for (int i = 2; i < code_data.length + 2; i++) {
            data_Send[i] = code_data[i - 2];
        }
        wirteCharacteristic(this.characteristic_Write, data_Send);
    }

    public static int weekTransform(int enable, int sun, int mon, int tue, int wed, int thu, int fri, int sat) {
        return (((((((0 | (enable << 7)) | (sun << 6)) | (sat << 5)) | (fri << 4)) | (thu << 3)) | (wed << 2)) | (tue << 1)) | mon;
    }

    public static byte[] short2bytes(short s) {
        byte[] bytes = new byte[2];
        for (int i = 1; i >= 0; i--) {
            bytes[i] = (byte) (s % 256);
            s = (short) (s >> 8);
        }
        return bytes;
    }

    public static byte[] int2Bytes_2Bytes(int value) {
        return new byte[]{(byte) ((65280 & value) >> 8), (byte) (value & 255)};
    }

    public static byte[] int2Bytes_4Bytes(int res) {
        return new byte[]{(byte) (res & 255), (byte) ((res >> 8) & 255), (byte) ((res >> 16) & 255), (byte) (res >>> 24)};
    }

    private short CRC_16(byte[] data) {
        short crc_result = (short) 0;
        int i = 0;
        while (i < data.length) {
            try {
                for (int j = 128; j != 0; j >>= 1) {
                    if ((32768 & crc_result) != 0) {
                        crc_result = (short) (((short) (crc_result << 1)) ^ 4129);
                    } else {
                        crc_result = (short) (crc_result << 1);
                    }
                    if ((data[i] & j) != 0) {
                        crc_result = (short) (crc_result ^ 4129);
                    }
                }
                i++;
            } catch (Exception e) {
                return (short) -1;
            }
        }
        return crc_result;
    }

    private String byteToString(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder(data.length);
        int length = data.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(String.format("%02X ", new Object[]{Byte.valueOf(data[i])}).toString());
        }
        return stringBuilder.toString();
    }
}
