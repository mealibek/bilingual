package com.example.bilingualb8.services.questions.impl;

import com.example.bilingualb8.dto.requests.option.OptionRequest;
import com.example.bilingualb8.dto.requests.option.OptionUpdateRequest;
import com.example.bilingualb8.dto.requests.questions.select_best_title.SelectBestTitleQuestionRequest;
import com.example.bilingualb8.dto.requests.questions.select_best_title.SelectBestTitleQuestionUpdateRequest;
import com.example.bilingualb8.dto.responses.SimpleResponse;
import com.example.bilingualb8.entity.Option;
import com.example.bilingualb8.entity.Question;
import com.example.bilingualb8.entity.Test;
import com.example.bilingualb8.enums.OptionType;
import com.example.bilingualb8.enums.QuestionType;
import com.example.bilingualb8.repositories.OptionRepository;
import com.example.bilingualb8.repositories.QuestionRepository;
import com.example.bilingualb8.repositories.TestRepository;
import com.example.bilingualb8.services.questions.SelectBestTitleQuestionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelectBestTitleQuestionServiceImpl implements SelectBestTitleQuestionService {
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final OptionRepository optionRepository;

    @Override
    public SimpleResponse save(SelectBestTitleQuestionRequest request) {
        log.info("Saving Select Best Title question: {}", request.getTitle());
        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new com.example.bilingualb8.exceptions.NotFoundException(String.format("Test with ID %s doesn't exist", request.getTestId())));

        Question question = new Question();
        // Set question fields
        question.setTitle(request.getTitle());
        question.setQuestionOrder(request.getQuestionOrder());
        question.setDuration(request.getDuration());
        question.setOptionType(OptionType.SINGLE);
        question.setTest(test);
        question.setPassage(request.getPassage());
        question.setQuestionType(QuestionType.SELECT_BEST_TITLE);
        question.setIsActive(request.getIsActive());

        // Set question options
        List<Option> options = new ArrayList<>();
        for (OptionRequest option : request.getOptions()) {
            Option optionInstance = new Option();
            optionInstance.setTitle(option.getTitle());
            optionInstance.setIsCorrect(option.getIsCorrect() != null ? option.getIsCorrect() : false);
            optionInstance.setQuestion(question);
            optionInstance.setOptionOrder(option.getOptionOrder());
            options.add(optionInstance);
        }
        question.setOptions(options);

        questionRepository.save(question);

        log.info("Question with title '{}' successfully saved", request.getTitle());
        return SimpleResponse.builder()
                .message(String.format("Question with ID %s successfully saved", question.getId()))
                .build();
    }

    @Transactional
    @Override
    public SimpleResponse update(Long id, SelectBestTitleQuestionUpdateRequest request) {
        log.info("Updating Select Best Title question: {}", request.getTitle());
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Question with ID %s was not found", id)));

        // Update question fields
        if (request.getTitle() != null) {
            question.setTitle(request.getTitle());
        }
        if (request.getDuration() != null) {
            question.setDuration(request.getDuration());
        }
        if (request.getQuestionOrder() != null) {
            question.setQuestionOrder(request.getQuestionOrder());
        }
        if (request.getPassage() != null) {
            question.setPassage(request.getPassage());
        }
        if (request.getIsActive() != null) {
            question.setIsActive(request.getIsActive());
        }

        // Update options
        List<Option> options = question.getOptions(); // Existing options
        List<OptionUpdateRequest> requestOptions = request.getOptions(); // Request options

        Map<Long, Option> optionMap = new HashMap<>();
        for (Option option : options) {
            optionMap.put(option.getId(), option);
        }

        for (OptionUpdateRequest optionRequest : requestOptions) {
            Long optionId = optionRequest.getId();
            Option option = optionMap.get(optionId);

            if (option == null) {
                // Create a new option
                option = new Option();
                option.setTitle(optionRequest.getTitle());
                option.setOptionOrder(optionRequest.getOptionOrder());
                option.setIsCorrect(optionRequest.getIsCorrect() != null ? optionRequest.getIsCorrect() : false);
                option.setQuestion(question);
                options.add(option);
            } else {
                // Update existing option
                if (optionRequest.getTitle() != null) {
                    option.setTitle(optionRequest.getTitle());
                }
                if (optionRequest.getOptionOrder() != null) {
                    option.setOptionOrder(optionRequest.getOptionOrder());
                }
                if (optionRequest.getIsCorrect() != null) {
                    option.setIsCorrect(optionRequest.getIsCorrect());
                }
                optionMap.remove(optionId);
            }
        }

        // Delete options not in the request
        for (Option option : optionMap.values()) {
            optionRepository.delete(option);
            options.remove(option);
        }

        // Save the updated question
        questionRepository.save(question);

        log.info("Question with ID {} successfully updated", id);
        return SimpleResponse.builder()
                .message(String.format("Question with ID %s updated successfully", id))
                .build();
    }
}