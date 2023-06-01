package com.bank.neoris.exception;

import com.bank.neoris.dto.ErrorResponseDto;
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

@ControllerAdvice
public class ExceptionCustomAdvice {

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto exceptionHandlerUserNotFound (UserNotFoundException exception){

        return new ErrorResponseDto(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto exceptionHandlerUserAlreadyExists (UserAlreadyExistsException exception){

        return new ErrorResponseDto(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(UserNotLegalAgeException.class)
    @ResponseStatus(HttpStatus.TOO_EARLY)
    public ErrorResponseDto exceptionHandlerUserNotLegalAge (UserNotLegalAgeException exception){

        return new ErrorResponseDto(exception.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(AccountsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto exceptionHandlerAccountNotFound (AccountsNotFoundException exception){

        return new ErrorResponseDto(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(AccountIsNotZeroException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponseDto exceptionHandlerAccountIsNotZero (AccountIsNotZeroException exception){

        return new ErrorResponseDto(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(TransactionValuesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto exceptionHandlerTransactionValues (TransactionValuesException exception){

        return new ErrorResponseDto(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(AccountNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto exceptionHandlerAccountNotExist (AccountNotExistsException exception){

        return new ErrorResponseDto(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto exceptionHandlerTransactionNotExist (TransactionNotFoundException exception){

        return new ErrorResponseDto(exception.getMessage());
    }
}
