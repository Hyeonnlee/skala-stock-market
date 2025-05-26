package com.example.skala.common;

import lombok.Data;

@Data
public class PagedList {
	private long total;
	private long count;
	private long offset;
	private Object list;
}
