package com.fr.function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fr.data.JobValue;
import com.fr.data.TotalSubmitJob;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

@SuppressWarnings("serial")
public class CustomizedSubmitUtil extends TotalSubmitJob {
	private static final String UPDATE = "update";
	private static final String DELETE = "delete";
	private static final String INSERT = "insert";
	private static final String url = "jdbc:mysql://127.0.0.1/frtest";
	private static final String name = "com.mysql.jdbc.Driver";
	private static final String user = "root";
	private static final String password = "123456";
	private Connection conn = null;
	private PreparedStatement pst = null;
	private ResultSet rs = null;
	private ResultSet querry(String sql) {
		try {
			Class.forName(name);// 指定连接类型
			conn = DriverManager.getConnection(url, user, password);// 获取连接
			pst = conn.prepareStatement(sql);// 准备执行语句
			rs =pst.executeQuery();
			
		} catch (Exception e) {
			System.out.println("exec queey error: "+sql);
			e.printStackTrace();
		} finally{
			try {
				this.conn.close();
				this.pst.close();
				this.rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rs;
	}

	@Override
	protected void doTotalJob(Data data, Calculator calculator) throws Exception {
		int num = data.getColumnCount();
		String sql=""; 
		for (int i = 0; i < num; i++) {
			data.getColumnName(i);// 获取对应属性的名称
		}
		// 各行数据的对象
		RowData rowData;
		// 循环各行数据
		for (int i = 0; i < data.getRowCount(); i++) {
			// 创建该行的对象
			rowData = new RowData();
			// 该行处理方式初始化
			String processfunc = "defu";
			// 循环该行的各个属性,并对行对象赋值
			for (int j = 0; j < data.getColumnCount(); j++) {
				Object value = data.getValueAt(i, j);
				int idState=JobValue.VALUE_STATE_DEFAULT;
				int nameState=JobValue.VALUE_STATE_DEFAULT;
				int descState=JobValue.VALUE_STATE_DEFAULT;
				int addressState=JobValue.VALUE_STATE_DEFAULT;
				if (value instanceof JobValue) {
					JobValue jobValue = (JobValue) value;
					if (j == 0) {
						idState=jobValue.getValueState();
						if("".equals(jobValue.getValue().toString())){
						rowData.setId(-1);	
						}else{
						rowData.setId(Integer.parseInt(jobValue.getValue().toString()));
						}
					} else if (j == 1) {
						nameState=jobValue.getValueState();
						rowData.setName(jobValue.getValue().toString());
					} else if (j == 2) {
						descState=jobValue.getValueState();
						rowData.setDescription(jobValue.getValue().toString());
					} else if (j == 3) {
						addressState=jobValue.getValueState();
						rowData.setAddress(jobValue.getValue().toString());
					}
					if(rowData.getId()==-1||idState==jobValue.VALUE_STATE_INSERT){
						processfunc=INSERT;
					}else if(idState==jobValue.VALUE_STATE_CHANGED||nameState==jobValue.VALUE_STATE_CHANGED||descState==jobValue.VALUE_STATE_CHANGED||addressState==jobValue.VALUE_STATE_CHANGED){
						processfunc=UPDATE;
					}else if(idState==jobValue.VALUE_STATE_DELETED){
						processfunc=DELETE;
					}
				}
			}
			// 通过jdbc进行数据处理
			if (UPDATE.equals(processfunc)) {
				System.out.println("update:" + rowData.getId() + "  " + rowData.getName() + "  "
						+ rowData.getDescription() + "  " + rowData.getAddress());
			//	sql="update";
			//	querry("");
			} else if (INSERT.equals(processfunc)) {
				System.out.println("insert:" + rowData.getId() + "  " + rowData.getName() + "  "
						+ rowData.getDescription() + "  " + rowData.getAddress());
			} else if (DELETE.equals(processfunc)) {
				System.out.println("delete:" + rowData.getId() + "  " + rowData.getName() + "  "
						+ rowData.getDescription() + "  " + rowData.getAddress());
			}
		}
	}
}
