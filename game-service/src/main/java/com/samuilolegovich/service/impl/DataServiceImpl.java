package com.samuilolegovich.service.impl;

import com.samuilolegovich.dto.AnswerToBetDto;
import com.samuilolegovich.repository.LottoRepo;
import com.samuilolegovich.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DataServiceImpl implements DataService {
    private final LottoRepo lottoRepo;

    public AnswerToBetDto getDataFoThisPage() {
        Long lottoNow = lottoRepo.findFirstByOrderByCreatedAtDesc().getTotalLottoCredits();
        return AnswerToBetDto.builder().lottoNow(lottoNow).build();
    }
}
