package com.mj.kit.excel;

@SuppressWarnings("serial")
public class ExcelException extends RuntimeException {

	public ExcelException(String message) {
		super(message);
	}

    public ExcelException(Throwable cause) {
        super(cause);
    }


}
