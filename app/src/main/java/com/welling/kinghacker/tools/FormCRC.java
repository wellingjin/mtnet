package com.welling.kinghacker.tools;

public class FormCRC {
	public static int get_crc16 (byte[] bufData, int buflen, byte[] pcrc)
	{
		int ret = 0;
		int CRC = 0x0000ffff;
		int POLYNOMIAL = 0x0000a001;
		int i, j;
		if (buflen == 0)
		{
			return ret;
		}
		for (i = 0; i < buflen; i++)
		{
			CRC ^= ((int)bufData[i] & 0x000000ff);
			for (j = 0; j < 8; j++)
			{
				if ((CRC & 0x00000001) != 0)
				{
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				}
				else
				{
					CRC >>= 1;
				}
			}			
		}
		System.out.println(Integer.toHexString(CRC));
		pcrc[0] = (byte)(CRC & 0x00ff);
		pcrc[1] = (byte)(CRC >> 8);		
		return CRC;
	}
}
