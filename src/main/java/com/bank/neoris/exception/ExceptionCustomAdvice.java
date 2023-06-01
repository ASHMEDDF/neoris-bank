package com.bank.neoris.exception;

import com.bank.neoris.exception.account.AccountIsNotZeroException;
import com.bank.neoris.exception.account.AccountNotExistsException;
import com.bank.neoris.exception.account.AccountsNotFoundException;
import com.bank.neoris.exception.transaction.TransactionNotFoundException;
import com.bank.neoris.exception.transaction.TransactionValuesException;
import com.bank.neoris.exception.user.UserAlreadyExistsException;
import com.bank.neoris.exception.user.UserNotFoundException;
import com.bank.neoris.exception.user.UserNotLegalAgeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionCustomAdvice {

    private static final String MESSAGE = "message";

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,String> exceptionHandlerUserNotFound (UserNotFoundException exception){

        Map<String,String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return errorMap;
    }

    @ResponseBody
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> exceptionHandlerUserAlreadyExists (UserAlreadyExistsException exception){

        Map<String,String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return errorMap;
    }

    @ResponseBody
    @ExceptionHandler(UserNotLegalAgeException.class)
    @ResponseStatus(HttpStatus.TOO_EARLY)
    public Map<String,String> exceptionHandlerUserNotLegalAge (UserNotLegalAgeException exception){

        Map<String,String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return errorMap;
    }


    @ResponseBody
    @ExceptionHandler(AccountsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,String> exceptionHandlerAccountNotFound (AccountsNotFoundException exception){

        Map<String,String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return errorMap;
    }

    @ResponseBody
    @ExceptionHandler(AccountIsNotZeroException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Map<String,String> exceptionHandlerAccountIsNotZero (AccountIsNotZeroException exception){

        Map<String,String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return errorMap;
    }

    @ResponseBody
    @ExceptionHandler(TransactionValuesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> exceptionHandlerTransactionValues (TransactionValuesException exception){

        Map<String,String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return errorMap;
    }

    @ResponseBody
    @ExceptionHandler(AccountNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,String> exceptionHandlerAccountNotExist (AccountNotExistsException exception){

        Map<String,String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return errorMap;
    }

    @ResponseBody
    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,String> exceptionHandlerTransactionNotExist (TransactionNotFoundException exception){

        Map<String,String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return errorMap;
    }
}
