package com.ibg.receipt.enums.application;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum AuditTypeEnum {

	APPLICATION("进件审核"),
	LOAN("借款审核");
	
	private String desc;
	
	private AuditTypeEnum(String desc) {
		this.desc = desc;
	}
	
	public static Optional<AuditTypeEnum> parse(String type) {
		return Stream.of(AuditTypeEnum.values()).filter(e -> e.name().equals(type)).findFirst();
	}
	
}
