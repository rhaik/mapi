package cn.wshz;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.io.FileUtils;

import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;

public class MyGenerator {

	public static void main(String[] args) {
		String fileName = "app_field.txt";
		Class <?> clazz = App.class;
		
		String data = getAllField(clazz);
		System.out.println(data);
		try {
			FileUtils.write(new File(fileName), data, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static <T> String getAllField(Class<T> clazz){
		StringBuilder sb = new StringBuilder(160);
		Field[] fields = clazz.getDeclaredFields();
		sb.append('(');
		for(Field field:fields){
			if((field.getModifiers() == 26 || field.getModifiers() == 25) == false){
				sb.append('`').append(field.getName()).append('`').append(",");
			}
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(')');
		sb.append(" values(");
		for(Field field:fields){
			if((field.getModifiers() == 26 || field.getModifiers() == 25) == false){
				sb.append('#').append('{').append(field.getName()).append('}').append(",");
			}
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(')');
		return sb.toString();
		
	}
}
