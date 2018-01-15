package com.fr.demo;

import java.util.ArrayList;
import java.util.List;
import com.fr.data.JobValue;
import com.fr.data.TotalVerifyJob;
import com.fr.data.Verifier;
import com.fr.data.Verifier.Status;
import com.fr.script.Calculator;

public class VerifyPeriodInBetween extends TotalVerifyJob {
	/*
	 * ����Ҫ�����˽�б���,�������ɸģ���ʾУ��״̬ 0 ��ʾУ��ɹ���Ĭ��У��״̬λΪ0 1 ��ʾУ��ʧ��
	 */
	private int type = 0;
	// ȡ�����н����
	List<RowData> result = new ArrayList<RowData>();
	// ���ڷ��ر�����Ϣ�ļ���
	List<String> message=new ArrayList<String>();
    // ����ȡ���ĵ�Ԫ���ֵת����string����
	public String toStrings(JobValue jv) {
		if (jv.getValue() instanceof String) {
			return String.valueOf(jv.getValue());
		} else {
			return "";
		}
	}
    //������ת��Ϊnumber ���ڼ���
	private int getnumperiod(String s) {
		return Integer.parseInt(s.substring(0, 4) + "" + s.substring(5, 7));

	}

	@Override
	protected void doTotalJob(Data data, Calculator cal) throws Exception {
		// ��ȡ��ȡ�Ķ�ά���ݳ���.
		int num = data.getRowCount();
		// ѭ���󽫶�ά���ݱ���Ϊ�������ڴ��������.
		for (int i = 0; i < num; i++) {
			RowData rowData = new RowData();
			rowData.setCompany(toStrings((JobValue) data.getValueAt(i, 0)));
			rowData.setDepartment(toStrings((JobValue) data.getValueAt(i, 1)));
			rowData.setPeriodStart(toStrings((JobValue) data.getValueAt(i, 2)));
			rowData.setPeriodEnd(toStrings((JobValue) data.getValueAt(i, 3)));
			rowData.setDeleteFlag(toStrings((JobValue) data.getValueAt(i, 4)));
			if("N".equals(rowData.getDeleteFlag())){
			result.add(rowData);
			}
		}
		//System.out.println("result����Ϊ:" + result.size());
		// for ѭ��ȡ��ÿһ�н��бȶ�
		for (int i = 0; i < num; i++) {
			//����ѭ������
			RowData dataloop = new RowData();
			dataloop = result.get(i);
			for (int j = 0; j < num; j++) {
				//���ڶԱȵ���
				RowData datacompare = new RowData();
				datacompare=result.get(j);
				//i=j������Ƕ���ͬ���н��бȽ�,���ܽ��бȽ�,i>j��i<j���ڽ����ظ��Ƚ�,ȡ���е�һ���ֽ��бȽ�.
				if (i < j) {
					//System.out.println("i=" + i + ",j=" + j);
					if (dataloop.getCompany().equals(datacompare.getCompany())
							&& dataloop.getDepartment().equals(datacompare.getDepartment())) {
						int f_f = getnumperiod(dataloop.getPeriodStart());
						int f_s = getnumperiod(dataloop.getPeriodEnd());
						int s_f = getnumperiod(datacompare.getPeriodStart());
						int s_s = getnumperiod(datacompare.getPeriodEnd());
						if ((f_f >= s_f && f_f <= s_s) || (f_s >= s_f && f_s <= s_s) || (s_f >= f_f && s_f <= f_s)
								|| (s_s >= f_f && s_s <= f_s)) {
							type=1;
							String log="��"+(i+1)+"�к�"+"��"+(j+1)+"���ظ�";
							message.add(log);
						}
					}
				}
			}
		}

	}

	public String getMessage() {
	if(type==1){
		return message.get(0);
	}else{
		return "У��ɹ�";
	}
	}

	public Status getType() {
		// ����У��״̬
		return Verifier.Status.parse(type);

	}

}
