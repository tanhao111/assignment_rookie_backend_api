package com.assignment.backend.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.assignment.backend.data.entities.Account;
import com.assignment.backend.data.repositories.AccountRepository;
import com.assignment.backend.dto.request.RegisterRequestDto;
import com.assignment.backend.dto.response.AccountResponseDto;
import com.assignment.backend.dto.response.SuccessResponse;
import com.assignment.backend.exceptions.ResourceNotFoundException;
import com.assignment.backend.services.AccountService;
import com.assignment.backend.utils.Utils;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private ModelMapper mapper;
    private PasswordEncoder encoder;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, ModelMapper modelMapper,
            PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.mapper = modelMapper;
        this.encoder = passwordEncoder;
    }

    @Override
    public List<AccountResponseDto> getAllAccount() {
        List<Account> listAccount = this.accountRepository.findAll();

        if (listAccount.isEmpty()) {
            throw new ResourceNotFoundException(Utils.NO_ACCOUNT);
        }

        List<AccountResponseDto> result = new ArrayList<>();

        for (Account acc : listAccount) {
            result.add(mapper.map(acc, AccountResponseDto.class));
        }

        return result;
    }

    @Override
    public AccountResponseDto getAccountById(int id) {
        Account acc = this.accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Utils.ACCOUNT_NOT_FOUND));

        return mapper.map(acc, AccountResponseDto.class);
    }

    @Override
    public ResponseEntity<?> deleteAccount(int id) {
        Account acc = this.accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Utils.ACCOUNT_NOT_FOUND));

        boolean status = acc.isStatus();
        acc.setStatus(!status);
        acc.setUpdateDate(new Date());
        this.accountRepository.save(acc);

        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK, Utils.ACC_DELETE));
    }

    @Override
    public ResponseEntity<?> updateAccount(int id, RegisterRequestDto dto) {
        Account acc = this.accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Utils.ACCOUNT_NOT_FOUND));

        mapper.map(dto, acc);
        acc.setUpdateDate(new Date());
        String newPass = dto.getPassword();
        acc.setPassword(encoder.encode(newPass));
        this.accountRepository.save(acc);

        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK, Utils.ACC_UPDATE));
    }

}
