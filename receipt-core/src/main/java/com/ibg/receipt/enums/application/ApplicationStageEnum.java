package com.ibg.receipt.enums.application;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum ApplicationStageEnum {

	APPLICATION("进件申请"),
	LOAN("进件借款");
	
	private String desc;
	
	private ApplicationStageEnum(String desc) {
		this.desc = desc;
	}
	
	public static Optional<ApplicationStageEnum> parse(String type) {
		return Stream.of(ApplicationStageEnum.values()).filter(e -> e.name().equals(type)).findFirst();
	}
	
}
