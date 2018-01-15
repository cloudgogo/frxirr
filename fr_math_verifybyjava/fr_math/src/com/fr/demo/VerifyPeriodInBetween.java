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
	 * 必须要定义此私有变量,变量名可改，表示校验状态 0 表示校验成功，默认校验状态位为0 1 表示校验失败
	 */
	private int type = 0;
	// 取到所有结果集
	List<RowData> result = new ArrayList<RowData>();
	// 用于返回报错信息的集合
	List<String> message=new ArrayList<String>();
    // 将获取到的单元格的值转换成string类型
	public String toStrings(JobValue jv) {
		if (jv.getValue() instanceof String) {
			return String.valueOf(jv.getValue());
		} else {
			return "";
		}
	}
    //将日期转换为number 便于计算
	private int getnumperiod(String s) {
		return Integer.parseInt(s.substring(0, 4) + "" + s.substring(5, 7));

	}

	@Override
	protected void doTotalJob(Data data, Calculator cal) throws Exception {
		// 获取获取的二维数据长度.
		int num = data.getRowCount();
		// 循环后将二维数据遍历为我们易于处理的类型.
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
		//System.out.println("result长度为:" + result.size());
		// for 循环取出每一行进行比对
		for (int i = 0; i < num; i++) {
			//用于循环的行
			RowData dataloop = new RowData();
			dataloop = result.get(i);
			for (int j = 0; j < num; j++) {
				//用于对比的行
				RowData datacompare = new RowData();
				datacompare=result.get(j);
				//i=j的情况是对相同的行进行比较,不能进行比较,i>j和i<j是在进行重复比较,取其中的一部分进行比较.
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
							String log="第"+(i+1)+"行和"+"第"+(j+1)+"行重复";
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
		return "校验成功";
	}
	}

	public Status getType() {
		// 返回校验状态
		return Verifier.Status.parse(type);

	}

}
