package com.school.bank_java.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.school.bank_java.code.AccountDetailType;
import com.school.bank_java.dao.IAccountDao;
import com.school.bank_java.exception.BankException;
import com.school.bank_java.vo.AccountDetailVo;
import com.school.bank_java.vo.AccountVo;

public class AccountService {
	IAccountDao dao;
	SqlSession session;
	
	public AccountService(IAccountDao dao, SqlSession session){
		this.dao = dao;
		this.session = session;
	}

//	public List<String> selectAccount_numbers(int uid) {
//		return dao.selectAccount_numbers(uid);
//	}

	public List<AccountVo> viewMyAccount(int user_uid) {
		return dao.selectAccountList(user_uid);
	}

	public boolean createAccount(int userUid) {
		int accountCount = dao.selectAccountCountByUserUid(userUid) + 1;
		String accountNumber = String.format("%05d%05d", userUid, accountCount);
		AccountVo accountVo = new AccountVo(userUid, accountNumber, 0);
		int resultCount = dao.insertAccount(accountVo);
		session.commit();
		return resultCount == 1 ? true : false;
	}

	public boolean deposit(int userUid, String accountNumber, int amount) {
		boolean resultCondition = executeDeposit(userUid, accountNumber, amount, true);
		if(resultCondition) {session.commit();} else {session.rollback();}
		
		return resultCondition;
	}

	private boolean executeDeposit(int userUid, String accountNumber, int amount, boolean isMyAccount) {
		boolean resultCondition = false;
		AccountVo myAccountVo = checkMyAndOtherAccount(userUid, accountNumber, isMyAccount);
		
		int totalAmount = checkTotalAmount(amount, myAccountVo);
		myAccountVo.setAmount( totalAmount );
		
		int rowCount = dao.updateAccount(myAccountVo);
		return rowCount == 1 ? true : false;
	}
	
//	private boolean doReciever(int userUid, String accountNumber, int amount) {
//		AccountVo myAccountVo = checkMyAccount(userUid, accountNumber);
//		
//		int totalAmount = checkTotalAmount(amount, myAccountVo);
//		myAccountVo.setAmount( totalAmount );
//		
//		int rowCount = dao.updateAccount(myAccountVo);
//		return rowCount == 1 ? true : false;
//	}
	

	private int checkTotalAmount(int amount, AccountVo myAccountVo) {
		boolean resultCondition;
		AccountDetailVo vo = new AccountDetailVo(myAccountVo.getUid(), AccountDetailType.SAVE, amount);
		int rowCount = dao.insertAccountDetail(vo);
		resultCondition = rowCount == 1 ? true : false;
		int totalAmount = myAccountVo.getAmount() + amount;
		if(totalAmount < 0){
			session.rollback();
			throw new BankException("잔액이 부족합니다.");
		}
		return totalAmount;
	}

	private AccountVo checkMyAndOtherAccount(int userUid, String accountNumber, boolean isMyAccount) {
		AccountVo accountVo = dao.selectAccountVoByAccountNumber(accountNumber);
		if(accountVo == null){ 
//			session.rollback();
			throw new BankException("계좌번호가 존재하지 않습니다.");
		}
		if(isMyAccount && accountVo.getUserUid() != userUid){ 
//			session.rollback();
			throw new BankException("본인의 계좌에만 입금/출금이 가능합니다.");
		}
		return accountVo;
	}

	public boolean withdrawal(int userUid, String accountNumber, int amount) {
		return deposit(userUid, accountNumber, amount * -1);
	}

	public boolean transfer(int userUid, String myAccountNumber, String otherAccountNumber, int amount) {
		
		boolean resultCondition1 = executeDeposit(userUid, myAccountNumber, amount * -1, true);
		boolean resultCondition2 = executeDeposit(userUid, otherAccountNumber, amount * 1, false);
		if(resultCondition1 && resultCondition2){ session.commit();}else{ session.rollback(); }
		return resultCondition1 && resultCondition2;
	}
}
