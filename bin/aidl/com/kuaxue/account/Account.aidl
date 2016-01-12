package com.kuaxue.account;

interface Account{
	String getToken();
	String getSignature(String nonc);
}