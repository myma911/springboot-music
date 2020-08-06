package cn.aaron911.music.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

@Configuration
public class UploadConfig {

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		// 文件最大50M,DataUnit提供5中类型B,KB,MB,GB,TB
		factory.setMaxFileSize(DataSize.of(50, DataUnit.MEGABYTES));
		/// 设置总上传数据总大小50M
		factory.setMaxRequestSize(DataSize.of(50, DataUnit.MEGABYTES));
		return factory.createMultipartConfig();
	}

}
