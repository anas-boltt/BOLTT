/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.boltt.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import com.android.boltt.R;
import com.android.boltt.band_sdk.DevDecode_X6;
import com.android.boltt.band_sdk.DevOperation_X6;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();

	public static DevOperation_X6 devOperation;
	public static DevDecode_X6 devDecode;

	public static int devOpCode = 0;
	public static int personalInfo_Type = 0;
	public static boolean isUsingPower = false;// �ĵ�ģʽ

	public int historyDate_Data_ID = 0;
	public int historyDetail_Data_ID = 0;

	public int historyDetail_Data_Block_ID = 1;// 1~7
	public int historyDetail_Data_Block_Hour_ID = 0;// 0~23

	public byte[] historyDate_Data = new byte[40];
	public byte[] historyDetail_Data = new byte[67];
	public int[][] historyDate_Map;
	public boolean isSetResetDevNotification = false;

	public int setNotification_ID = 0;
	public boolean isSetNotification_OK = false;

	// private UUID airUpgrade_UUID =
	// UUID.fromString("0000f018-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Notify = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	// private UUID uUID_Service_Main =
	// UUID.fromString("00001530-1212-efde-1523-785feabcd123");
	//
	// private UUID uUID_Cha_Operation =
	// UUID.fromString("00001531-1212-efde-1523-785feabcd123");
	// private UUID uUID_Cha_AirUpgrade_Img =
	// UUID.fromString("00001532-1212-efde-1523-785feabcd123");

	// ������
	private UUID uUID_Service_Main = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

	private UUID uUID_Cha_Operation = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Cha_AirUpgrade_Img = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

	private UUID uUID_Cha_Crc_CHARACTERISTIC_CONFIG = new UUID(45088566677504L, -9223371485494954757L);

	private BluetoothGattService service_Main, service_Reset;
	private BluetoothGattCharacteristic cha_Operation, cha_Write_Image, cha_ResetDev;

	// ͨ��
	private UUID uUID_Service_Dev_Info = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Service_Dev_Info_Battery = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");

	private UUID uUID_Cha_Dev_Info_Fireware = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Cha_Dev_Info_Hardware = UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Cha_Dev_Info_Software = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Cha_Dev_Info_Manufacturer = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Cha_Dev_Info_Battery = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

	private BluetoothGattService service_Dev_Info, service_Dev_Info_Battery;
	private BluetoothGattCharacteristic cha_Info_Fireware, cha_Info_Hardware, cha_Info_Software, cha_Info_Manufacturer, cha_Info_Battery;

	// �Զ���
	private UUID uUID_Service_Dev_Operiation = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");

	private UUID uUID_Cha_Operiation_Read = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Cha_Operiation_NotificationData = UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Cha_Operiation_Write = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");

	private BluetoothGattService service_Dev_Operiation;
	private BluetoothGattCharacteristic cha_Operiation_Read, cha_Operiation_NotificationData, cha_Operiation_Write;

	// ��ʱ��Ϣ
	private UUID uUID_Service_Dev_Operiation_Current = UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb");
	private UUID uUID_Cha_Operiation_Read_Current = UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb");

	private BluetoothGattService service_Dev_Operiation_Current;
	private BluetoothGattCharacteristic cha_Operiation_Read_Current;

	// ������̬����
	public static boolean isDFUReset = false;// �ֻ��ѽ��������ģʽ����Ҫ����
	public static boolean isWriteImageSize = false;// �Ƿ���д���ļ���С
	public static boolean isWriteCRC_Version = false;// �Ƿ���д��CRC

	public static boolean isFirstDiscovery = false;// ��һ����������

	public static boolean isStartUpgrade = false;// �Ƿ���ʽ��
	public static boolean isFastUpdate = false;// �Ƿ������ģʽ
	public static boolean isReSend = false;// �Ͽ��ٴη���
	public static int isReSendCount = 0;// ���·��ͼ�¼

	public static int fastCount = 200;// ������һ�η�����ݰ����
	public static int airUpgradeCount = 0;// ��ǰд��ڼ�����
	public static int receiveCount = 0;

	public static int packageCount = -1;// �������ͷ�ļ���
	public static int lastPackageLength = -1;
	public static int historyDataCount = 0;
	public static byte[][] xval;// ������飨����ͷ�ļ���
	public static byte[] xval_Last;// ������飨����ͷ�ļ���

	public byte[] data;// ԭʼ���

	byte[] fileNameDescription = new byte[32];
	byte[] fileCreateTime = new byte[8];
	byte[] version = new byte[4];
	byte[] imgageSize = new byte[4];
	byte[] crc = new byte[4];
	byte[] crc_version = new byte[8];

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

	// -----
	public final static String READ_DEV_Version = "com.example.bluetooth.le.READ_DEV_Version";
	public final static String READ_DEV_Mac_Serial = "com.example.bluetooth.le.READ_DEV_Mac_Serial";
	public final static String READ_DEV_Battery = "com.example.bluetooth.le.READ_DEV_Battery";
	public final static String READ_DEV_CurrentDate = "com.example.bluetooth.le.READ_DEV_CurrentDate";
	public final static String READ_DEV_CurrentSportData = "com.example.bluetooth.le.CurrentSportData";
	public final static String READ_DEV_AlarmClock = "com.example.bluetooth.le.AlarmClock";

	public final static String READ_DEV_PersonalInfo = "com.example.bluetooth.le.PersonalInfo";
	public final static String READ_DEV_HistoryData = "com.example.bluetooth.le.READ_DEV_HistoryData";

	public final static String READ_DEV_OPERATION = "com.example.bluetooth.le.READ_DEV_OPERATION";

	public android.os.Handler activityHandler;

	private String tip_Steps, tip_Distances, tip_Calories;

	public void setHandler(android.os.Handler h) {
		activityHandler = h;

		tip_Steps = getResources().getString(R.string.tips_steps);
		tip_Distances = getResources().getString(R.string.tips_distances);
		tip_Calories = getResources().getString(R.string.tips_calories);

	}

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {

				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
				if (isDFUReset) {
					isDFUReset = false;
					activityHandler.obtainMessage(2).sendToTarget();
				}

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
				if (isStartUpgrade) {// �Ͽ����ӣ��ٴ����ϣ�����������У�����д�뾵���ļ�
					activityHandler.obtainMessage(0, "�豸�Ͽ�������ͣ�����Ӽ�����").sendToTarget();
				}
				if (isDFUReset) {

					close();
					initialize();
					connect(mBluetoothDeviceAddress);
				}
			}
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			// TODO Auto-generated method stub
			super.onReadRemoteRssi(gatt, rssi, status);
			System.out.println("Rssi=" + rssi);
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			// TODO Auto-generated method stub
			super.onDescriptorRead(gatt, descriptor, status);
			String uuid = descriptor.getUuid().toString();
			System.out.println("onDescriptorRead-uuid=" + uuid);

			byte[] data = descriptor.getValue();

			System.out.println("onDescriptorRead-" + byteToString(data));
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			// ֪ͨ���Է��أ�
			super.onDescriptorWrite(gatt, descriptor, status);
			String uuid = descriptor.getUuid().toString();
			System.out.println("onDescriptorWrite-uuid=" + uuid);
			byte[] data = descriptor.getValue();
			System.out.println("onDescriptorWrite-" + byteToString(data));
			if (isSetResetDevNotification) {
				isSetResetDevNotification = false;
				byte[] data1 = switchAddr(mBluetoothDeviceAddress, true);

				// System.out.println("��λ");
				writeCharacteristic(cha_ResetDev, data1);// �豸��λxx
			}
			System.out.println("setNotification_ID=" + setNotification_ID);

			switch (setNotification_ID) {
			case 1:
				if (cha_Operiation_Read != null) {
					setNotification_ID = 2;
					setCharacteristicNotification(cha_Operiation_Read, true);
				}
				break;
			case 2:
				setNotification_ID = 0;
				String intentAction = READ_DEV_OPERATION;
				broadcastUpdate(intentAction);
				setCharacteristicNotification(cha_Info_Battery, true);// �����Զ���ʾ

				break;
			case 3:
				setNotification_ID = 0;
				isSetNotification_OK = true;

				break;
			default:
				break;
			}
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			// TODO Auto-generated method stub
			super.onReliableWriteCompleted(gatt, status);

			System.out.println("onReliableWriteCompleted-uuid=" + status);

		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicWrite(gatt, characteristic, status);

			String uuid = characteristic.getUuid().toString();
			byte[] data = characteristic.getValue();// ��ȡ�������
			// System.out.println("д�뷵��-uid=" + uuid);
			// System.out.println("д�뷵��-״̬=" + status);
			// System.out.println("д�뷵��-data=" + byteToString(data));
			System.out.println("д������״̬=" + byteToString(characteristic.getValue()));
			// ��������
			if (uuid.equals(uUID_Cha_AirUpgrade_Img.toString())) {
				if (isFastUpdate) {// ������������
					return;
				}
				if (status == BluetoothGatt.GATT_SUCCESS) {// д��ɹ���д����һ�����
					if (isStartUpgrade) {// д�뾵���ļ�
						airUpgradeCount++;
						activityHandler.obtainMessage(12, airUpgradeCount).sendToTarget();
						if (airUpgradeCount < (packageCount - 1)) {// û��д��̼�
							System.out.println("write image count=" + airUpgradeCount);
							writeCharacteristic(cha_Write_Image, xval[airUpgradeCount]);
						} else if (airUpgradeCount == (packageCount - 1)) {// ���һ����ݰ����
							System.out.println("write image count=" + airUpgradeCount);
							writeCharacteristic(cha_Write_Image, xval_Last);
						} else {
							System.out.println("write image completed");
							activityHandler.obtainMessage(0, "write image completed").sendToTarget();
							isStartUpgrade = false;

						}
						return;
					}

				} else {
				}
			}

		}

		/**
		 * ֪ͨ���Ըı�
		 */
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			String uuid = characteristic.getUuid().toString();
			System.out.println("Notify-uuid=" + uuid);

			byte[] data = characteristic.getValue();

			System.out.println("Notify-" + byteToString(data));
			// activityHandler.obtainMessage(0, "Notify-" +
			// byteToString(data)).sendToTarget();

			// ��ʱ�����ϴ�
			if (uuid.equals(uUID_Cha_Operiation_Read_Current.toString())) {
				int currentStep = devDecode.decode_CurrentValue_Auto(data);
				// System.out.println("currentStep:" + currentStep);
				activityHandler.obtainMessage(0, tip_Steps + ":" + currentStep).sendToTarget();

			} else if (uuid.equals(uUID_Cha_Dev_Info_Battery.toString())) {// ���������ϴ�
				int value = data[0];
				// System.out.println("---------------���������ϱ�=" + value);
				activityHandler.obtainMessage(0, "����:" + value + "%").sendToTarget();
			}else if (uuid.equals(uUID_Cha_Operiation_Read.toString())) {

				System.out.println("opCode=" + devOpCode);
				switch (devOpCode) {
				case 1:
					String[] mac_sn = devDecode.decode_MAC_SN(data);
					if ((mac_sn != null) && (mac_sn.length == 2)) {
						activityHandler.obtainMessage(0, "MAC=" + mac_sn[0] + "  SN=" + mac_sn[1]).sendToTarget();
					}

					devOpCode = 0;
					break;
				case 2:
					int[] currentInfo = devDecode.decode_CurrentValue(data);
					if ((currentInfo != null) && (currentInfo.length == 3)) {
						activityHandler.obtainMessage(0, tip_Steps + ":" + currentInfo[0] + "  " + tip_Distances + ":" + currentInfo[1] + " " + tip_Calories + ":" + currentInfo[2]).sendToTarget();
					}
					devOpCode = 0;
					break;
				case 5:// ��ȡʱ��
					int[] date_Read = devDecode.decode_Date_Time(data);
					System.out.println("Device Time ��" + date_Read[0] + "-" + date_Read[1] + "-" + date_Read[2] + " " + date_Read[3] + ":" + date_Read[4]);
					activityHandler.obtainMessage(0, "Device Time:" + date_Read[0] + "-" + date_Read[1] + "-" + date_Read[2] + " " + date_Read[3] + ":" + date_Read[4]).sendToTarget();

					devOpCode = 0;
					break;
				case 6:// ��ȡ����
					int[] alarmClock_Read = devDecode.decode_AlarmClock(data);
					int[] enableData = DevDecode_X6.weekTransTo(alarmClock_Read[2]);
					System.out.println("AlarmClock  ID:" + alarmClock_Read[0] + " type:" + alarmClock_Read[1] + " enable:" + enableData[7] + " Time:" + alarmClock_Read[3] + ":" + alarmClock_Read[4] + " remindTime:" + alarmClock_Read[5]);
					activityHandler.obtainMessage(0, "AlarmClock ID:" + alarmClock_Read[0] + " type:" + alarmClock_Read[1] + " enable:" + enableData[0] + " (1~7):" + enableData[0] + enableData[1] + enableData[2] + enableData[3] + enableData[4] + enableData[5] + enableData[6] + " Time:" + alarmClock_Read[3] + ":" + alarmClock_Read[4] + " remindTime:" + alarmClock_Read[5]).sendToTarget();
					devOpCode = 0;
					break;
				case 7:// ��ȡ������Ϣ
					int[] persondata = devDecode.decode_PersonalInfo(data, personalInfo_Type);
					if (persondata == null) {
						devOpCode = 0;
						break;
					}
					if (personalInfo_Type == 1) {// 01 ������Ϣ�����/����/�Ա�/���䣩
						System.out.println("Person  Hight:" + persondata[0] + " Weight:" + persondata[1] + " Gender��" + persondata[2] + " Age:" + persondata[3]);
						activityHandler.obtainMessage(0, "Person  Hight:" + persondata[0] + " Weight:" + persondata[1] + " Gender��" + persondata[2] + " Age:" + persondata[3]).sendToTarget();

					} else if (personalInfo_Type == 2) { // 02 ��������
						System.out.println("Sedentary remind:" + persondata[0] + "S");
						activityHandler.obtainMessage(0, "Sedentary remind:" + persondata[0] + "S").sendToTarget();

					} else if (personalInfo_Type == 3) { // 03 Ŀ�경��
						System.out.println("Target Steps:" + persondata[0]);
						activityHandler.obtainMessage(0, "Target Steps:" + persondata[0]).sendToTarget();

					} else if (personalInfo_Type == 4) { // 04 ˯��ʱ��
						System.out.println("Read Sleepping Time=" + persondata[0] + ":" + persondata[1] + "~" + persondata[2] + ":" + persondata[3]);
						activityHandler.obtainMessage(0, "Read Sleepping Time=" + persondata[0] + ":" + persondata[1] + "~" + persondata[2] + ":" + persondata[3]).sendToTarget();

					} else if (personalInfo_Type == 5) { // 05 ��������
						System.out.println("Disconnect reminder=" + persondata[0] + "  Time format=" + persondata[1] + " �գ� Type��" + persondata[2]);
						activityHandler.obtainMessage(0, "Disconnect reminder=" + persondata[0] + "  Time format=" + persondata[1] + " �գ� Type��" + persondata[2]).sendToTarget();

					} else if (personalInfo_Type == 6) { // 06����ʱ��
						System.out.println("Do not disturb  enable=" + persondata[0] + " time��" + persondata[1] + ":" + persondata[2] + "~" + persondata[3] + ":" + persondata[4]);
						activityHandler.obtainMessage(0, "Do not disturb  enable=" + persondata[0] + " time��" + persondata[1] + ":" + persondata[2] + "~" + persondata[3] + ":" + persondata[4]).sendToTarget();
					} else if (personalInfo_Type == 7) { // 07 ���� ����ѡ��
						System.out.println("Language Code:" + persondata[0] + "\n 0 EN/CN/JP,1 Ko,2 Many languages,3 ISO8859");
						activityHandler.obtainMessage(0, "Language Code:" + persondata[0] + "\n 0 EN/CN/JP,1 Ko,2 Many languages,3 ISO8859").sendToTarget();
					} else if (personalInfo_Type == 8) { // 08 ��Ļˮƽ��ת
						System.out.println("Screen flip:" + persondata[0]);
						activityHandler.obtainMessage(0, "Screen flip:" + persondata[0]).sendToTarget();
					} else if (personalInfo_Type == 9) { // 09 ̧����������
						System.out.println(" Auto bright screen:" + persondata[0] + "\n0 Closed,2 Auto(Portrait),3 Auto(Horizontal)");
						activityHandler.obtainMessage(0, " Auto bright screen:" + persondata[0] + "\n0 Closed,1 Auto(Portrait),3 Auto(Horizontal)").sendToTarget();
					}
					devOpCode = 0;
					break;
				case 8:// д��ʱ��
					if (byteToString(data).replace(" ", "").equals("250121")) {
						activityHandler.obtainMessage(0, "Write time success").sendToTarget();
						System.out.println("Write time success");
					}
					devOpCode = 0;
					break;
				case 9:// д�������Ϣ
					if (byteToString(data).replace(" ", "").contains("250220")) {
						activityHandler.obtainMessage(0, "Write personal information successfully").sendToTarget();
						System.out.println("Write personal information successfully");
					}
					devOpCode = 0;
					break;
				case 10:// д������
					if (byteToString(data).replace(" ", "").contains("250222")) {
						activityHandler.obtainMessage(0, "Write alarm clock successfully").sendToTarget();
						System.out.println("Write alarm clock successfully");
					}
					devOpCode = 0;
					break;
				case 51:// ��ȡ��ʷ���ӳ���
					if (historyDate_Data_ID == 0) {

						if (data.length < 20) {// ���ڲ�ȫ��ֻ��1/2/3�����
							historyDate_Data_ID = 0;// ����

							historyDate_Map = devDecode.decode_HistoryRecodeDate(data, data.length);
							StringBuffer showBuffer = new StringBuffer();
							for (int j = 0; j < historyDate_Map.length; j++) {
								showBuffer.append("\n" + historyDate_Map[j][0] + "Block  Date=" + historyDate_Map[j][1] + "/" + historyDate_Map[j][2] + "/" + historyDate_Map[j][3]);
							}
							activityHandler.obtainMessage(0, "HistoryData  DateMaping:" + showBuffer.toString()).sendToTarget();
							devOpCode = 0;
						} else {
							historyDate_Data_ID = 1;
							for (int i = 0; i < data.length; i++) {
								historyDate_Data[i] = data[i];
							}
						}

						break;
					} else if (historyDate_Data_ID == 1) {
						historyDate_Data_ID = 0;
						int datalength = 20 + data.length;

						for (int i = 20; i < datalength; i++) {
							historyDate_Data[i] = data[i - 20];
						}

						historyDate_Map = devDecode.decode_HistoryRecodeDate(historyDate_Data, datalength);
						StringBuffer showBuffer = new StringBuffer();
						for (int j = 0; j < historyDate_Map.length; j++) {
							showBuffer.append("\n" + historyDate_Map[j][0] + "Block  Date=" + historyDate_Map[j][1] + "/" + historyDate_Map[j][2] + "/" + historyDate_Map[j][3]);
						}
						activityHandler.obtainMessage(0, "HistoryData  DateMaping:" + showBuffer.toString()).sendToTarget();
						devOpCode = 0;
						break;
					}
					break;
				case 52:
					// System.out.println(historyDetail_Data_ID + " ��ݰ�");
					// activityHandler.obtainMessage(0, historyDetail_Data_ID +
					// " ��ݰ�").sendToTarget();
					if (historyDetail_Data_ID == 0) {
						activityHandler.obtainMessage(0, historyDetail_Data_Block_ID + "Block  " + historyDetail_Data_Block_Hour_ID + "Hour").sendToTarget();
						// System.out.println("--------------��飺" +
						// historyDetail_Data_Block_ID + " Сʱ=" +
						// historyDetail_Data_Block_Hour_ID);
						if (data.length < 15) {
							// ��Сʱ�����
							System.out.println(historyDetail_Data_Block_Hour_ID + "Hour����No Data");
							activityHandler.obtainMessage(0, historyDetail_Data_Block_Hour_ID + "Hour����No Data").sendToTarget();

							historyDetail_Data_Block_Hour_ID++;
							if (historyDetail_Data_Block_Hour_ID == 24) {// Сʱ���
								historyDetail_Data_Block_Hour_ID = 0;
								if (historyDate_Map[historyDetail_Data_Block_ID - 1][0] != 0) {// �и������
									historyDetail_Data_Block_ID++;
								}

							}
							if ((historyDetail_Data_Block_ID > 7) || (historyDate_Map[historyDetail_Data_Block_ID - 1][0] == 0)) {// 7��������
								activityHandler.obtainMessage(0, "Over").sendToTarget();
								System.out.println("-----------Over");
								devOpCode = 0;
								break;
							}
							devOperation.readHistoryRecodeDatail((byte) historyDetail_Data_Block_ID, (byte) historyDetail_Data_Block_Hour_ID);

							break;
						}

						historyDetail_Data_ID = 1;
						for (int i = 0; i < data.length; i++) {
							historyDetail_Data[i] = data[i];
						}
						break;
					} else if (historyDetail_Data_ID == 1) {
						historyDetail_Data_ID = 2;
						for (int i = 20; i < 20 + data.length; i++) {
							historyDetail_Data[i] = data[i - 20];
						}
						break;
					} else if (historyDetail_Data_ID == 2) {
						historyDetail_Data_ID = 3;
						for (int i = 40; i < 40 + data.length; i++) {
							historyDetail_Data[i] = data[i - 40];
						}
						break;
					} else if (historyDetail_Data_ID == 3) {
						historyDetail_Data_ID = 0;

						for (int i = 60; i < 60 + data.length; i++) {
							historyDetail_Data[i] = data[i - 60];
						}

						int[][] steps = devDecode.decode_HistoryRecodeDatail(historyDetail_Data);

						int[] steps_temp = new int[steps.length - 1];
						int[] distances = new int[steps.length - 1];
						int[] calories = new int[steps.length - 1];
						for (int i = 1; i < steps.length; i++) {
							steps_temp[i - 1] = steps[i][1];
							activityHandler.obtainMessage(0, (i * 2 - 1) + "~" + (i * 2) + "min data=" + steps[i][1] + "  type=" + steps[i][0]).sendToTarget();
						}
						distances = devDecode.getHistoryDistance(steps_temp, 170);
						calories = devDecode.getHistoryCalories(distances, 60);

						// opCode = 0;
						historyDetail_Data_Block_Hour_ID++;
						if (historyDetail_Data_Block_Hour_ID == 24) {// Сʱ���
							historyDetail_Data_Block_Hour_ID = 0;
							if (historyDate_Map[historyDetail_Data_Block_ID - 1][0] != 0) {// �и������
								historyDetail_Data_Block_ID++;
							}
						}
						if ((historyDetail_Data_Block_ID > 7) || (historyDate_Map[historyDetail_Data_Block_ID - 1][0] == 0)) {
							System.out.println("-----------Over");
							devOpCode = 0;
							break;
						}
						devOperation.readHistoryRecodeDatail((byte) historyDetail_Data_Block_ID, (byte) historyDetail_Data_Block_Hour_ID);
						break;
					}

					break;
				case 500:// д����Ϣͷ֪ͨ����
					// ִ�У�����
					activityHandler.obtainMessage(3).sendToTarget();
					devOpCode = 0;
					break;
				default:
					break;
				}

			}
			// opCode = 0;
			// activityHandler.obtainMessage(2, uuid).sendToTarget();

			String dataString = byteToString(data);
			// activityHandler.obtainMessage(3, dataString).sendToTarget();

			if (dataString.substring(0, 2).contains("11")) {// ������ݰ��������
				receiveCount++;
				// System.out.println("receiveCount=" + (receiveCount %
				// fastCount) + " " + airUpgradeCount + "/" + packageCount);
				// Ŀǰ�����ø÷���д��
				if ((receiveCount % fastCount) == 0) {// ���Ҫ��

					for (int i = 0; (i < fastCount) && (airUpgradeCount < packageCount); i++) {// Ҫ��С�����һ����ݰ�֮ǰ
						activityHandler.obtainMessage(12, airUpgradeCount).sendToTarget();

						writeCharacteristic(cha_Write_Image, xval[airUpgradeCount]);
						airUpgradeCount++;
						isReSendCount = airUpgradeCount;
						if (airUpgradeCount == (packageCount - 1)) {// ���һ����ݰ��
							activityHandler.obtainMessage(12, airUpgradeCount).sendToTarget();
							writeCharacteristic(cha_Write_Image, xval_Last);
							break;
						}
					}
				}
			}

			if (uuid.equals(uUID_Cha_Operation.toString())) {// ���������
				// if (status == BluetoothGatt.GATT_SUCCESS) {// д��ɹ���д����һ�����
				if (dataString.contains("10 01 01 ")) {
					System.out.println("д���������02-initialize DFU Parameters");
					activityHandler.obtainMessage(0, "д���������02").sendToTarget();
					writeCharacteristic(cha_Operation, new byte[] { (byte) 2 });// control
					activityHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							isWriteCRC_Version = true;
							System.out.println("д���2��ͷ�ļ�-crc_�汾");
							writeCharacteristic(cha_Write_Image, crc_version);
						}
					}, 500);

				} else if (dataString.contains("10 02 01 ")) {

					System.out.println("д���������03-receive firmware image");
					activityHandler.obtainMessage(0, "д���������03").sendToTarget();
					writeCharacteristic(cha_Operation, new byte[] { (byte) 3 });// control
					activityHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							isStartUpgrade = true;
							System.out.println("д�뾵���ļ���=" + airUpgradeCount);
							if (isFastUpdate) {
								for (int i = 0; i < fastCount; i++) {
									activityHandler.obtainMessage(12, airUpgradeCount).sendToTarget();
									writeCharacteristic(cha_Write_Image, xval[airUpgradeCount]);
									airUpgradeCount++;
								}
							} else {
								writeCharacteristic(cha_Write_Image, xval[airUpgradeCount]);
							}

						}
					}, 500);

				} else if (dataString.contains("10 03 01 ")) {
					System.out.println("д���������04-vlidate firmware");
					activityHandler.obtainMessage(0, "д���������04-vlidate firmware").sendToTarget();
					activityHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							writeCharacteristic(cha_Operation, new byte[] { 4 });
						}
					}, 500);

				} else if (dataString.contains("10 04 01 ")) {
					System.out.println("д���������05-��λ");
					activityHandler.obtainMessage(0, "д���������05-��λ").sendToTarget();
					writeCharacteristic(cha_Operation, new byte[] { 5 });
				} else if (dataString.contains("10 07 01 ")) {// �Ͽ���������ȡ�豸�Ѿ�������ݰ����
					activityHandler.obtainMessage(0, "�ѽ�����ݸ���=" + byteToString(data).substring(9)).sendToTarget();

					if (data.length == 7) {
						byte re0001 = data[6];
						byte re0010 = data[5];
						byte re0100 = data[4];
						byte re1000 = data[3];

						System.out.println("-------�ѽ�����ݰ�=" + byteToString(new byte[] { re0001, re0010, re0100, re1000 }));

						int recount = toInt(new byte[] { re0001, re0010, re0100, re1000 }) / 20;
						activityHandler.obtainMessage(0, "�ѽ�����ݰ�=" + recount + "    �ѷ�����ݰ�=" + airUpgradeCount).sendToTarget();
						System.out.println("�ѽ�����ݰ�=" + recount + "    �ѷ�����ݰ�=" + airUpgradeCount);
						if ((recount == (airUpgradeCount + 0)) || (recount == (airUpgradeCount + 1))) {
							if (recount == (airUpgradeCount + 1)) {
								airUpgradeCount++;
							}
							activityHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									writeCharacteristic(cha_Write_Image, xval[airUpgradeCount]);
									if (isFastUpdate) {

										isReSend = false;
										for (int i = isReSendCount; (i < fastCount) && (airUpgradeCount < packageCount); i++) {// Ҫ��С�����һ����ݰ�֮ǰ
											activityHandler.obtainMessage(12, airUpgradeCount).sendToTarget();
											// activityHandler.postDelayed(new
											// Runnable() {
											// @Override
											// public void run() {
											writeCharacteristic(cha_Write_Image, xval[airUpgradeCount]);
											airUpgradeCount++;

											if (airUpgradeCount == (packageCount - 1)) {// ���һ����ݰ��
												activityHandler.obtainMessage(12, airUpgradeCount).sendToTarget();
												writeCharacteristic(cha_Write_Image, xval_Last);
												// break;
											}
											// }
											// }, 30);

										}

									}
								}
							}, 500);
						} else {
							activityHandler.obtainMessage(0, "��ݰ�����쳣����������").sendToTarget();
						}
					}

				}
				// }
			}
			// broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}

		// ��byte����bRefArrתΪһ������,�ֽ�����ĵ�λ�����͵ĵ��ֽ�λ
		public int toInt(byte[] b) {
			int mask = 0xff;
			int temp = 0;
			int n = 0;
			for (int i = 0; i < 4; i++) {
				n <<= 8;
				temp = b[i] & mask;
				n |= temp;
			}
			return n;
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
				if (isStartUpgrade) {
					if (isFirstDiscovery) {
						isFirstDiscovery = false;
						// discoveryService();
						// } else {
						isReSend = true;
						initUpdate();
						activityHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								activityHandler.obtainMessage(0, "���·��;����ļ�").sendToTarget();

								// ����֪ͨ,����UUID���Ըı�ʱ����֪ͨ����onCharacteristicChanged�н������
								setCharacteristicNotification(cha_Operation, true);
								activityHandler.postDelayed(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										writeCharacteristic(cha_Operation, new byte[] { (byte) 7 });// control
									}
								}, 1000);

								// airUpgradeCount++;
								// writeCharacteristic(imagecha,
								// xval[airUpgradeCount]);
							}
						}, 500);
					}
				}

			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

			String uid = characteristic.getUuid().toString();
			byte[] data = characteristic.getValue();
			System.out.println("�յ�UUID��" + uid);
			System.out.println("�յ���" + byteToString(data));
			if (status == BluetoothGatt.GATT_SUCCESS) {// �ɹ���ȡ

				// System.out.println("У����" + VerifyData(data, data.length));

				if (uid == null) {// �ж��Ƿ�Ϊ��,��ͷ�Ƿ����
					return;
				}
				if (uid.equals(uUID_Cha_Dev_Info_Battery.toString())) {
					System.out.println("device battery=" + (int) data[0] + "%");
					activityHandler.obtainMessage(0, "device battery=" + (int) data[0] + "%").sendToTarget();
				} else if (uid.equals(uUID_Cha_Dev_Info_Fireware.toString())) {
					try {
						String srt2 = new String(data, "UTF-8");
						activityHandler.obtainMessage(0, "Firmware version=" + srt2).sendToTarget();
						System.out.println("Firmware version=" + srt2);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (uid.equals(uUID_Cha_Dev_Info_Software.toString())) {
					try {
						String srt2 = new String(data, "UTF-8");
						activityHandler.obtainMessage(0, "Soft version=" + srt2).sendToTarget();
						System.out.println("Soft version=" + srt2);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} else {

			}
		}

	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		// This is special handling for the Heart Rate Measurement profile. Data
		// parsing is
		// carried out as per profile specifications:
		// http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
		
		//UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())
		if (false) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
				Log.d(TAG, "Heart rate format UINT16.");
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
				Log.d(TAG, "Heart rate format UINT8.");
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			Log.d(TAG, String.format("Received heart rate: %d", heartRate));
			intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));
				intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
			}
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	public void discoveryService() {
		mBluetoothGatt.discoverServices();
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
			Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

		// boolean re = device.createBond();
		// System.out.println("=============��Խ��=" + re);

		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}

		isFirstDiscovery = true;

		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);

		devOperation = new DevOperation_X6(mBluetoothGatt);// ��ʼ������
		devDecode = new DevDecode_X6();

		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		boolean re = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		System.out.println("setCharacteristicNotification=" + re);

		// This is specific to Heart Rate Measurement.
		// if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		// BluetoothGattDescriptor descriptor =
		// characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));

		// BluetoothGattDescriptor descriptor =
		// characteristic.getDescriptor(uUID_Cha_Crc_CHARACTERISTIC_CONFIG);
		// BluetoothGattDescriptor descriptor =
		// characteristic.getDescriptor(uUID_Notify);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uUID_Notify);

		descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		mBluetoothGatt.writeDescriptor(descriptor);
		// }

		// -------------------------

	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	/**
	 * ��ȡ�豸��ǰ�˶����
	 */
	public void readCurrentValue() {
		try {

			if (mBluetoothGatt == null) {
				return;
			}
			if (mConnectionState == STATE_DISCONNECTED) {// ���δ����
				return;
			}
			System.out.println("Bluetooth-��ȡ��ǰ��------------");
			readCharacteristic(cha_Operation);

		} catch (Exception e) {
			System.out.println("Bluetooth-��ȡ��ǰ�˶�����쳣------------");
		}
	}

	/**
	 * �豸��λ
	 */
	public void resetDev_Normal(int type) {
		// 01��λϵͳ������02�ظ�Ĭ�ϲ���03�����ʷ���
		try {

			if (mBluetoothGatt == null) {
				return;
			}
			if (mConnectionState == STATE_DISCONNECTED) {// ���δ����
				return;
			}

			System.out.println("��λ");

			// byte[] temp = switchAddr(mBluetoothDeviceAddress, true);
			// byte[] data = new byte[] { (byte) 0x40, (byte) 0x01, temp[0],
			// temp[1], temp[2], temp[3], temp[4], temp[5] };

			byte[] data_Reset = new byte[] { (byte) 0x40, (byte) type };// 01��λϵͳ������02�ظ�Ĭ�ϲ���03�����ʷ���

			devOperation.writeCode(data_Reset, false);
			// System.out.println(byteToString(data));
			isDFUReset = true;
		} catch (Exception e) {
			System.out.println("�豸��λ�쳣------------" + e.toString());
		}
	}

	// �������쳣����λ
	public void resetDev_Upgrading() {
		try {
			initUpdate();
			// ����֪ͨ,����UUID���Ըı�ʱ����֪ͨ����onCharacteristicChanged�н������
			setCharacteristicNotification(cha_Operation, true);
			Thread.sleep(500);// ˯��500ms
			System.out.println("��λ------------");
			writeCharacteristic(cha_Operation, new byte[] { 6 });// 05����̼�����λ��06ϵͳ��λ
		} catch (Exception e) {
			System.out.println("�豸��λ�쳣------------" + e.toString());
		}

	}

	/**
	 * �豸��λ
	 */
	public void resetDev_Upgrade() {
		try {

			if (mBluetoothGatt == null) {
				return;
			}
			if (mConnectionState == STATE_DISCONNECTED) {// ���δ����
				return;
			}

			// pairService = mBluetoothGatt.getService(reset_service_UUID);
			// service_Main =
			// mBluetoothGatt.getService(uUID_Service_Dev_Operiation);
			// cha_ResetDev =
			// service_Main.getCharacteristic(uUID_Cha_Operiation_Write);

			isDFUReset = false;// �ֻ��ѽ��������ģʽ����Ҫ����
			isWriteImageSize = false;// �Ƿ���д���ļ���С
			isWriteCRC_Version = false;// �Ƿ���д��CRC

			isFirstDiscovery = false;// ��һ����������

			isStartUpgrade = false;// �Ƿ���ʽ��
			isFastUpdate = false;// �Ƿ������ģʽ
			isReSend = false;// �Ͽ��ٴη���
			isReSendCount = 0;// ���·��ͼ�¼

			fastCount = 200;// ������һ�η�����ݰ����
			airUpgradeCount = 0;// ��ǰд��ڼ�����
			receiveCount = 0;

			packageCount = -1;// �������ͷ�ļ���
			lastPackageLength = -1;
			historyDataCount = 0;

			// initUpdate();
			// byte[] data = new byte[] { (byte) 0x25, (byte) 0x03, (byte) 0x40,
			// (byte) 0x01, (byte) 0x44, (byte) 0xdc, (byte) 0x26 };
			// mBluetoothDeviceAddress
			// byte[] data = new byte[] { (byte) 0xd4, (byte) 0xb0, (byte) 0x82,
			// (byte) 0x18, (byte) 0xe7, (byte) 0x46 };

			// ����֪ͨ,����UUID���Ըı�ʱ����֪ͨ����onCharacteristicChanged�н������
			// setCharacteristicNotification(cha_Operiation_Write, true);
			isSetResetDevNotification = true;
			// Thread.sleep(500);
			// byte[] data = new byte[] { (byte) 0x46, (byte) 0xe7, (byte) 0x18,
			// (byte) 0x82, (byte) 0xb0, (byte) 0xd4 };

			System.out.println("��λ");

			byte[] temp = switchAddr(mBluetoothDeviceAddress, true);
			byte[] data = new byte[] { (byte) 0x40, (byte) 0x01, temp[0], temp[1], temp[2], temp[3], temp[4], temp[5] };

			devOperation.writeCode(data, false);
			// System.out.println(byteToString(data));
			isDFUReset = true;
		} catch (Exception e) {
			System.out.println("�豸��λ�쳣------------" + e.toString());
		}
	}

	// ��MAC��ַ����ת�����û��̼���
	private byte[] switchAddr(String add, boolean isReverse) {// �Ƿ���
		byte[] data = new byte[6];

		add = add.replaceAll(" ", "");
		add = add.replaceAll(":", "");

		String bit[] = new String[6];
		bit[0] = add.substring(0, 2);
		bit[1] = add.substring(2, 4);
		bit[2] = add.substring(4, 6);
		bit[3] = add.substring(6, 8);
		bit[4] = add.substring(8, 10);
		bit[5] = add.substring(10, 12);
		//
		// for (int i = 0; i < bit.length; i++) {
		// System.out.println("---ת�����" + bit[i]);
		// // data[i] = (byte) (Integer.parseInt(("0x" + bit[i])));
		// }
		if (isReverse) {
			add = bit[5] + bit[4] + bit[3] + bit[2] + bit[1] + bit[0];
		} else {
			add = bit[0] + bit[1] + bit[2] + bit[3] + bit[4] + bit[5];
		}

		// System.out.println("ת�����" + add.replaceAll(":", ""));
		data = HexString2Bytes(add);
		// System.out.println("ת�����" + byteToString(data));

		return data;
	}

	/**
	 * ��ָ���ַ�src����ÿ�����ַ�ָ�ת��Ϊ16������ʽ �磺"2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 */
	public static byte[] HexString2Bytes(String src) {
		int length = src.length() / 2;
		byte[] ret = new byte[length];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < length; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * ������ASCII�ַ�ϳ�һ���ֽڣ� �磺"EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * �̼���λ
	 */
	public void resetHardWare() {
		try {

			if (mBluetoothGatt == null) {
				return;
			}
			if (mConnectionState == STATE_DISCONNECTED) {// ���δ����
				return;
			}
			if (mBluetoothGatt != null) {
				service_Reset = mBluetoothGatt.getService(uUID_Service_Dev_Operiation);

				if (service_Main != null) {
					cha_ResetDev = service_Reset.getCharacteristic(uUID_Cha_Operiation_Write);
				}
			}
			System.out.println("��λ");
			writeCharacteristic(cha_Operation, new byte[] { (byte) 6 });

		} catch (Exception e) {
			System.out.println("Bluetooth-��ȡ��ǰ�˶�����쳣------------");
		}
	}

	public void airUpgrade(byte[] d, boolean isFastUpdate) {
		isFastUpdate = isFastUpdate;
		Update(d);
	}

	/**
	 * �̼���
	 */
	public void Update(byte[] d) {
		data = d;

		initUpdate();
		// System.out.println("д���������f-01");
		// wirteCharacteristic(controlcha, new byte[] { (byte) 1, (byte) 0 });//
		// control

		// mBluetoothGatt.readRemoteRssi();
		System.out.println("��ʼ��=------------");
		System.out.println("pairService=" + service_Main);
		System.out.println("paircha=" + cha_Write_Image);
		System.out.println("controlcha=" + cha_Operation);
		System.out.println("crc_CLIENT_CHARACTERISTIC_CONFIG=" + uUID_Cha_Crc_CHARACTERISTIC_CONFIG.toString());

		int index_x = 0;
		for (int i = 0; i < 16; i++) {
			fileNameDescription[i] = data[i];
		}
		index_x = index_x + 16;
		for (int i = index_x; i < index_x + 8; i++) {
			fileCreateTime[i - index_x] = data[i];
		}
		index_x = index_x + 8;

		for (int i = index_x; i < index_x + 4; i++) {
			version[i - index_x] = data[i];

		}
		index_x = index_x + 4;
		for (int i = index_x; i < index_x + 4; i++) {
			imgageSize[i - index_x] = data[i];
		}

		index_x = index_x + 4;
		for (int i = index_x; i < index_x + 4; i++) {
			crc[i - index_x] = data[i];
		}

		index_x = index_x + 4;
		for (int i = 0; i < 4; i++) {
			crc_version[i] = crc[i];
		}
		for (int i = 4; i < 8; i++) {
			crc_version[i] = version[i - 4];
		}

		packageCount = (data.length - 256) / 20;
		lastPackageLength = (data.length - 256) % 20;

		if (lastPackageLength != 0) {// �������Ŀ��1
			packageCount++;
		}

		System.out.println("�������=" + packageCount + "   ����=" + lastPackageLength);
		activityHandler.obtainMessage(0, "�������=" + packageCount + "   ����=" + lastPackageLength).sendToTarget();
		activityHandler.obtainMessage(11, packageCount + 1).sendToTarget();

		System.out.println("fileNameDescription:\n" + byteToString(fileNameDescription));
		System.out.println("fileCreateTime:\n" + byteToString(fileCreateTime));
		System.out.println("version:\n" + byteToString(version));
		System.out.println("imgageSize:\n" + byteToString(imgageSize));
		System.out.println("crc:\n" + byteToString(crc));
		System.out.println("crc_version:\n" + byteToString(crc_version));

		try {

			if (mBluetoothGatt == null) {
				return;
			}
			if (mConnectionState == STATE_DISCONNECTED) {// ���δ����
				return;
			}

			if (service_Main == null) {
				return;
			}

			if (cha_Write_Image == null) {
				return;
			}

			// ����֪ͨ,����UUID���Ըı�ʱ����֪ͨ����onCharacteristicChanged�н������
			setCharacteristicNotification(cha_Operation, true);
			Thread.sleep(1000);// ˯��500ms

			xval = new byte[packageCount][20];

			for (int i = 0; i < xval.length - 1; i++) {
				for (int j = 0; j < 20; j++) {
					int index = 256 + 20 * i + j;
					xval[i][j] = data[index];
				}
			}

			// ���һ����ݰ�
			if (lastPackageLength == 0) {// ������
				lastPackageLength = 20;
			}
			xval_Last = new byte[lastPackageLength];
			for (int i = 0; i < lastPackageLength; i++) {
				int index = 256 + 20 * (xval.length - 1) + i;
				// xval[xval.length - 1][i] = data[index];

				xval_Last[i] = data[index];
			}
			Thread.sleep(500);// ˯��500ms
			System.out.println("д���������01");
			activityHandler.obtainMessage(0, "д���������01").sendToTarget();
			writeCharacteristic(cha_Operation, new byte[] { (byte) 0x01 });// control
			// point
			// 10��������豸����һ��
			Thread.sleep(500);// ˯��500ms
			System.out.println("д���������08-��ѯ��ݰ����");
			writeCharacteristic(cha_Operation, new byte[] { (byte) 8, (byte) fastCount, (byte) 0 });// control

			Thread.sleep(500);// ˯��500ms
			isWriteImageSize = true;
			System.out.println("д���һ��ͷ�ļ�-�����С");
			writeCharacteristic(cha_Write_Image, imgageSize);// image-size(4bytes)]

		} catch (Exception e) {
			System.out.println("Bluetooth-д��֪ͨ�쳣------------");
			activityHandler.obtainMessage(0, "Bluetooth-д��֪ͨ�쳣").sendToTarget();
		}

	}

	public void initUpdate() {
		if (mBluetoothGatt != null) {
			service_Main = mBluetoothGatt.getService(uUID_Service_Main);

			if (service_Main != null) {
				cha_Write_Image = service_Main.getCharacteristic(uUID_Cha_AirUpgrade_Img);
				cha_Operation = service_Main.getCharacteristic(uUID_Cha_Operation);
			}
		}
	}

	public void inintUUID() {
		try {
			if (mBluetoothGatt != null) {
				System.out.println("--------------------��ʼ��UUID");

				service_Dev_Operiation = mBluetoothGatt.getService(uUID_Service_Dev_Operiation);
				service_Dev_Operiation_Current = mBluetoothGatt.getService(uUID_Service_Dev_Operiation_Current);

			//	chnage if statment text by Sanjiv "DeviceControlActivity.mDeviceName.toLowerCase().contains(\"dfu\")"

				if (false) {

				} else {

					service_Dev_Info = mBluetoothGatt.getService(uUID_Service_Dev_Info);

					cha_Info_Fireware = service_Dev_Info.getCharacteristic(uUID_Cha_Dev_Info_Fireware);
					cha_Info_Hardware = service_Dev_Info.getCharacteristic(uUID_Cha_Dev_Info_Hardware);
					cha_Info_Software = service_Dev_Info.getCharacteristic(uUID_Cha_Dev_Info_Software);
					cha_Info_Manufacturer = service_Dev_Info.getCharacteristic(uUID_Cha_Dev_Info_Manufacturer);

					service_Dev_Info_Battery = mBluetoothGatt.getService(uUID_Service_Dev_Info_Battery);
					cha_Info_Battery = service_Dev_Info_Battery.getCharacteristic(uUID_Cha_Dev_Info_Battery);
					// ��ģʽ
					cha_Operiation_Read = service_Dev_Operiation.getCharacteristic(uUID_Cha_Operiation_Read);
					cha_Operiation_NotificationData = service_Dev_Operiation.getCharacteristic(uUID_Cha_Operiation_NotificationData);
					cha_Operiation_Write = service_Dev_Operiation.getCharacteristic(uUID_Cha_Operiation_Write);
					devOperation.setWriteCharacteristic(cha_Operiation_Write);

					cha_Operiation_Read_Current = service_Dev_Operiation_Current.getCharacteristic(uUID_Cha_Operiation_Read_Current);

					devOperation.setWriteCharacteristic_NotificationData(cha_Operiation_NotificationData);
				}

				setNotificatio();
			}
		} catch (Exception e) {
			System.out.println("--------------------��ʼ��UUID�쳣" + e.toString());
		}
	}

	/**
	 * ����notification֪ͨ
	 */
	public void setNotificatio() {
		try {
			setNotification_ID = 1;
			// if (cha_Operiation_Read_Current != null) {
			// ����֪ͨ,����UUID���Ըı�ʱ����֪ͨ����onCharacteristicChanged�н������
			setCharacteristicNotification(cha_Operiation_Read_Current, true);
			// }

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void readDevHardInfo() {
		// inintUUID();
		// ����֪ͨ,����UUID���Ըı�ʱ����֪ͨ����onCharacteristicChanged�н������
		readCharacteristic(cha_Info_Hardware);
	}

	public void readDevSoftInfo() {
		readCharacteristic(cha_Info_Software);
	}

	public void readDevFirmwareInfo() {
		readCharacteristic(cha_Info_Fireware);
	}

	public void readDevManufacturer() {
		readCharacteristic(cha_Info_Manufacturer);
	}

	public void readDevBatteryInfo() {
		readCharacteristic(cha_Info_Battery);
	}

	public void writeOpCode(byte[] value) {
		writeCharacteristic(cha_Operation, value);// control
	}

	public void writeImage(byte[] value) {
		writeCharacteristic(cha_Write_Image, value);// image
	}

	/**
	 * д������
	 */
	private void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
		if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null)) {
			Log.e(TAG, "BluetoothAdapter not initialized");
			return;
		}
		if (mConnectionState == STATE_DISCONNECTED) {// ���δ����
			return;
		}
		try {
			characteristic.setValue(value);
			characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);// 1,2,4
			// System.out.println("BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE="
			// + BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			// System.out.println("BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT="
			// + BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
			// System.out.println("BluetoothGattCharacteristic.WRITE_TYPE_SIGNED="
			// + BluetoothGattCharacteristic.WRITE_TYPE_SIGNED);

			this.mBluetoothGatt.writeCharacteristic(characteristic);
		} catch (Exception e) {
			disconnect();// �Ͽ�����
			// close();// �ر�����
		}

	}

	/**
	 * byte������תString��ʾ
	 */
	private String byteToString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder(data.length);
		for (byte byteChar : data) {
			stringBuilder.append(String.format("%02X ", byteChar).toString());
		}
		return stringBuilder.toString();
	}
}
