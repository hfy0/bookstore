package com.example.account.validation;

import com.example.account.mapper.ud.AccountUDMapper;
import org.example.domain.account.Account;
import org.example.domain.security.AuthenticAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.Collection;


/**
 * 用户对象校验器（注解对应的校验器）
 * <p>
 * 如:
 * 新增用户时，判断该用户对象是否允许唯一;
 * 在修改用户时，判断该用户是否存在
 *
 * @param <T>
 */
@Component
public abstract class AccountValidation<T extends Annotation> implements ConstraintValidator<T, Account> {

    @Autowired
    protected AccountUDMapper accountUDMapper;

    public static class ExistsAccountValidator extends AccountValidation<ExistsAccount> {
        @Override
        public boolean isValid(Account value, ConstraintValidatorContext context) {
            return accountUDMapper.findById(value.getId()) != null;
        }
    }

    public static class AuthenticatedAccountValidator extends AccountValidation<AuthenticatedAccount> {
        @Override
        public boolean isValid(Account value, ConstraintValidatorContext context) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if ("anonymousUser".equals(principal)) {
                return false;
            } else {
                AuthenticAccount loginUser = (AuthenticAccount) principal;
                return value.getId().equals(loginUser.getId());
            }
        }
    }

    public static class UniqueAccountValidator extends AccountValidation<UniqueAccount> {
        @Override
        public boolean isValid(Account value, ConstraintValidatorContext context) {
            Collection<Account> collection = accountUDMapper.findByUsernameOrEmailOrTelephone(value.getUsername(), value.getEmail(), value.getTelephone());
            return collection.isEmpty() || collection.size() == 0;
        }
    }

    public static class NotConflictAccountValidator extends AccountValidation<NotConflictAccount> {
        @Override
        public boolean isValid(Account value, ConstraintValidatorContext context) {
            Collection<Account> collection = accountUDMapper.findByUsernameOrEmailOrTelephone(value.getUsername(), value.getEmail(), value.getTelephone());
            // 将用户名、邮件、电话改成与现有完全不重复的，或者只与自己重复的，就不算冲突
            return collection.isEmpty() || (collection.size() == 1 && collection.iterator().next().getId().equals(value.getId()));
        }
    }
}
