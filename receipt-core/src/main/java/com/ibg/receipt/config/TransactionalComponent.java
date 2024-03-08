package com.ibg.receipt.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * 事务管理工具
 *
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2020-04-08
 */
@Slf4j
@Component
public class TransactionalComponent {

	@Autowired
	private TransactionTemplate transactionTemplate;

	/**
	 * 事务执行器
	 *
	 * @param iTransactional
	 */
	public <T> T doTransactionWithResult(ITransactionalWithResult<T> iTransactional) {
		return transactionTemplate.execute((status) -> iTransactional.execute());
	}

	/**
	 * 事务执行器
	 *
	 * @param iTransactional
	 */
	public void doTransaction(ITransactional iTransactional) {
		transactionTemplate.execute((status) -> {
			iTransactional.execute();
			return Boolean.TRUE;
		});
	}

	/**
	 * 事务执行器
	 *
	 * @param iTransactional
	 */
	public <T> T doTransactionWithResultAndRetry(ITransactionalWithResult<T> iTransactional) {
		return this.doTransactionWithResultAndRetry(iTransactional, 3, Lists.newArrayList(Exception.class));
	}

	/**
	 * 事务执行器
	 *
	 * @param iTransactional
	 * @param retryCount
	 * @param exceptions
	 * @param <T>
	 * @return
	 */
	public <T> T doTransactionWithResultAndRetry(ITransactionalWithResult<T> iTransactional, int retryCount, List<Class<? extends Throwable>> exceptions) {
		for (int i = 0; i < retryCount; i++) {
			try {
				return doTransactionWithResult(iTransactional);
			} catch (Exception exception) {
				// 不包含报错
				if (this.isNotAssignableFrom(exception, exceptions)) {
					throw exception;
				}
				log.info("发送可重试事务异常: {}, 即将进入重试, 当前重试次数为: {}", exception.getClass(), i + 1);
			}
		}
		return doTransactionWithResult(iTransactional);
	}

	/**
	 * 执行器
	 * @param iTransactional
	 * @param retryCount
	 * @param exceptions
	 * @param <T>
	 * @return
	 */
	public <T> T doNoTransactionWithResultAndRetry(ITransactionalWithResult<T> iTransactional, int retryCount, List<Class<? extends Throwable>> exceptions) {
		for (int i = 0; i < retryCount; i++) {
			try {
				return iTransactional.execute();
			} catch (Exception exception) {
				// 不包含报错
				if (this.isNotAssignableFrom(exception, exceptions)) {
					throw exception;
				}
				log.info("发送可重试事务异常: {}, 即将进入重试, 当前重试次数为: {}", exception.getClass(), i + 1);
			}
		}
		return iTransactional.execute();
	}

	/**
	 * 事务执行器
	 *
	 * @param iTransactional
	 * @param retryCount
	 * @param exceptions
	 */
	public void doTransactionWithRetry(ITransactional iTransactional, int retryCount, List<Class<? extends Throwable>> exceptions) {
		for (int i = 0; i < retryCount; i++) {
			try {
				doTransaction(iTransactional);
				return;
			} catch (Exception exception) {
				// 不包含报错
				if (this.isNotAssignableFrom(exception, exceptions)) {
					throw exception;
				}
				log.info("发送可重试事务异常: {}, 即将进入重试, 当前重试次数为: {}", exception.getClass(), i + 1);
			}
		}
		doTransaction(iTransactional);
	}

	/**
	 * 执行器
	 * @param iTransactional
	 * @param retryCount
	 * @param exceptions
	 */
	public void doNoTransactionWithRetry(ITransactional iTransactional, int retryCount, List<Class<? extends Throwable>> exceptions) {
		for (int i = 0; i < retryCount; i++) {
			try {
				iTransactional.execute();
				return;
			} catch (Exception exception) {
				// 不包含报错
				if (this.isNotAssignableFrom(exception, exceptions)) {
					throw exception;
				}
				log.info("发送可重试事务异常: {}, 即将进入重试, 当前重试次数为: {}", exception.getClass(), i + 1);
			}
		}
		iTransactional.execute();
	}

	/**
	 * 事务执行器
	 *
	 * @param iTransactional
	 */
	public void doTransactionWithRetry(ITransactional iTransactional) {
		this.doTransactionWithRetry(iTransactional, 3, Lists.newArrayList(Exception.class));
	}

	/**
	 * 都不符合
	 *
	 * @param exception
	 * @param exceptions
	 * @return
	 */
	private boolean isNotAssignableFrom(Exception exception, List<Class<? extends Throwable>> exceptions) {
		return exceptions.stream().noneMatch(ex -> ex.isAssignableFrom(exception.getClass()));
	}

	/**
	 * 异步执行器
	 *
	 * @param iAsync
	 */
	@Async
	public void doAsync(IAsync iAsync) {
		iAsync.execute();
	}


	/**
	 * 异步接口
	 */
	public interface IAsync {

		/**
		 * 执行
		 */
		void execute();
	}

	/**
	 * 事务接口
	 */
	public interface ITransactionalWithResult<T> {

		/**
		 * 执行
		 *
		 * @return
		 */
		T execute();
	}

	/**
	 * 事务接口
	 */
	public interface ITransactional {

		/**
		 * 执行
		 *
		 * @return
		 */
		void execute();
	}
}
