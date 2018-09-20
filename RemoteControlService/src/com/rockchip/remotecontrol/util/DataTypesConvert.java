/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Filename:    DataTypesConvert.java  
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2011-11-17 ����03:33:56  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2011-11-17      xwf         1.0         create
*******************************************************************/   


package com.rockchip.remotecontrol.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class DataTypesConvert {

	/**
	 * �ַ�����ת��Ϊ�ַ�
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuilder sb = new StringBuilder(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * ���ֽ���ֵת��Ϊ����
	 * 
	 * @param bArray
	 * @return
	 * @throws IOException 
	 * @throws StreamCorruptedException 
	 */
	public static final Object bytesToObject(byte[] bytes) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(in);
		Object o = oi.readObject();
		oi.close();
		return o;
	}
	
	/**
	 * ��byte���ת��Ϊintֵ
	 * @param data
	 * @return
	 */
	public static int changeByteToInt(byte[] data) {
		return changeByteToInt(data, 0, data.length-1);
	}
	
	/**
	 * ��byte����ת��Ϊint
	 * @param data
	 * @param start ��
	 * @param end ����
	 * @return
	 */
	public static int changeByteToInt(byte[] data, int start, int end) {
		int result = 0;
		
		for (int i = start; i <= end; i++) {
			result = result << 8;
			result = result | (data[i] & 0xff);
			
		}
		return result;
	}

	/**
	 * sortType 1 ��ʾ�Ӹ�λ����λ 2 ��ʾ�ӵ�λ����λ
	 * 
	 * @param bArray
	 * @return
	 */
	public static long changeByteToLong(byte[] data, int startNum, int endNum,
			int sortType) {
		long TNumber = 0;
		if (sortType == 1)
			for (int i = startNum; i <= endNum; i++) {
				TNumber = TNumber << 8;
				TNumber = TNumber | (data[i] & 0xff);
			}
		else
			for (int i = endNum; i >= startNum; i--) {
				TNumber = TNumber << 8;
				TNumber = TNumber | (data[i] & 0xff);
			}
		return TNumber;
	}


	/**
	 * sortType 1 ��ʾ�Ӹ�λ����λ 2 ��ʾ�ӵ�λ����λ
	 * 
	 * @param bArray
	 *            , int...
	 * @return
	 */
	public static long changeBCDByteToLong(byte[] data, int startNum, int endNum,
			int sortType) {
		long TNumber = 0;
		if (sortType == 1)
			for (int i = startNum; i <= endNum; i++) {
				byte tmpByte = data[i];
				int ten = (tmpByte&0xF0)>>>4;
				int dec = tmpByte& 0x0f;
				int tmp = ten*10+dec;
				TNumber = TNumber + tmp
				* (long) Math.pow(100, (endNum - i));
			}
		else
			for (int i = endNum; i >= startNum; i--) {
				byte tmpByte = data[i];
				int ten = (tmpByte&0xF0)>>>4;
				int dec = tmpByte& 0x0f;
				int tmp = ten*10+dec;
				TNumber = TNumber + tmp
							* (long) Math.pow(100, (i - startNum));
			}
		return TNumber;
	}
	public static byte[] changeHexStrTobytes(String[] str) {
		byte[] data = new byte[str.length];
		for (int i = 0; i < str.length; i++) {
			String hexStr = "0x" + str[i];
			data[i] = Byte.decode(hexStr).byteValue();
			System.out.println(data[i]);
		}
		return data;
	}
	
	/**
	 * ����ת���ɶ�Ӧ��λ���byte����,len��ʾ��Ҫת�������鳤��
	 * @param num
	 * @param len
	 * @return byte[]
	 */
	public static byte[] changeIntToByte(int num, int len) {
		return changeLongToByte(num, len, 1);
	}

	/**
	 * ������ת���ɶ�Ӧ��λ���byte����,len��ʾ��Ҫת�������鳤��
	 * @param num
	 * @param len
	 * @return byte[]
	 */
	public static byte[] changeLongToByte(long num, int len) {
		return changeLongToByte(num, len, 1);
	}

	/**
	 * ������ת���ɶ�Ӧ��λ���byte����,len��ʾ��Ҫת�������鳤��,sortType 1��ʶ��λ��ǰ 2 ��ʶ��λ��ǰ
	 * 
	 * @param long , int,sottType
	 * @return byte[]
	 */
	public static byte[] changeLongToByte(long num, int len, int sortType) {
		byte[] data = new byte[len];
		if (sortType == 1) {
			for (int i = 0; i < len; i++) {
				data[i] = (byte) ((num >> (8*(len-1-i))) & 0xff); 
			}
		} else {
			for (int i = len-1; i >= 0; i--) {
				data[i] = (byte) ((num >> (8*i)) & 0xff);
			}
		}
		return data;
	}
	
	//floatתbyte[]
	public static byte[] floatToByte(float v) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        byte[] ret = new byte [4];
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(v);
        bb.get(ret);
        return ret;
	}

	//byte[]תfloat
	public static float byteToFloat(byte[] v){
        ByteBuffer bb = ByteBuffer.wrap(v);
        FloatBuffer fb = bb.asFloatBuffer();
        return fb.get();
	}

	/**
	 * ����У��ͣ��ж���ݴ����Ƿ�����
	 * @param b
	 * @return
	 */
	public static boolean checkSum(byte[] b) {
		int sum = 0;
		// �������ĵڶ�λ��ʼ����
		for (int i = 4; i < b.length - 2; i++) {
			sum = sum + b[i];
		}
		int res = sum / 256;
		sum = sum - res * 256;
		// ת��Ϊ�ֽ�4�Ƚ�
		if (b.length < 2) {
			return false;
		}
		if ((byte) sum == b[b.length - 2]) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ����У��ͣ����У����
	 * 
	 * @param msg
	 * @return
	 */
	public static byte getCheckSum(byte[] b) {
		if(b==null) return 0;
		int sum = 0;
		for (int i = 4; i < b.length - 2; i++) {
			sum = sum + b[i];
		}
		return (byte) sum;
	}
	
	
	/**
	* byte to int
	* 
	* @param b
	*            ��ת�����ֽ�����
	*        
	* @return
	*/
	public static int byte2int(byte b[]) {
	   return b[3] & 0xff | (b[2] & 0xff) << 8
	     | (b[1] & 0xff) << 16 | (b[0] & 0xff) << 24;
	}

	/**
	* int to byte
	* 
	* @param n��ת�������α�
	*
	*/
	public static byte[] int2byte(int n) {
		byte buf[] = new byte[4];
	   buf[0] = (byte) ((n >> 24) & 0xff);
	   buf[1] = (byte) ((n >> 16) & 0xff);
	   buf[2] = (byte) ((n >> 8) & 0xff);
	   buf[3] = (byte) (n & 0xff);
	   return buf;
	}

	/**
	* @return type void
	* @param n
	*            ��ת����short��
	*/
	public static byte[] short2byte(short n) {
		byte buf[] = new byte[2];
	    buf[0] = (byte) (n >> 8);
	    buf[1] = (byte) n;
	    return buf;
	}
	
	public static short byte2short(byte buf[]){
		return (short)((buf[1] & 0xff) | (buf[0] & 0xff )<< 8);
	}

	public static long bytes2long(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 8; i++) {
			res <<= 8;
		    temp = b[i] & mask;
		    res |= temp;
		}
		return res;
	}

	public static byte[] long2bytes(long num) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (num >>> (56 - i * 8));
		}
		return b;
	}

 

}
