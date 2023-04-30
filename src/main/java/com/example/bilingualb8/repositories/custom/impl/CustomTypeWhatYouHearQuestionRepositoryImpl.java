package com.example.bilingualb8.repositories.custom.impl;
import com.example.bilingualb8.dto.responses.questions.type_what_you_hear.TypeWhatYouHearQuestionResponse;
import com.example.bilingualb8.enums.FileType;
import com.example.bilingualb8.enums.QuestionType;
import com.example.bilingualb8.repositories.custom.CustomTypeWhatYouHearQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomTypeWhatYouHearQuestionRepositoryImpl implements CustomTypeWhatYouHearQuestionRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TypeWhatYouHearQuestionResponse> getAllTypeWhatYouHearQuestion() {
        String sql = """
                select q.id            as id,
                       q.title         as title,
                       q.duration      as duration,
                       q.statement     as statement,
                       q.question_type as questionType,
                       f.id as fileId,
                       f.file_url      as fileUrl,
                       f.file_type     as fileType,
                       q.correct_answer as correctAnswer,
                       t.id as testId
                from questions q
                         join tests t on t.id = q.test_id
                         join files f on q.id = f.question_id
                """;
        return jdbcTemplate.query(sql, (resultSet, i) -> new TypeWhatYouHearQuestionResponse(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getInt("duration"),
                        QuestionType.valueOf(resultSet.getString("question_type")),
                        resultSet.getLong("id"),
                        resultSet.getLong("fileId"),
                        FileType.valueOf(resultSet.getString("fileType")),
                        resultSet.getString("fileUrl"),
                        resultSet.getString("correctAnswer"),
                        resultSet.getLong("testId")));
    }

    @Override
    public Optional<TypeWhatYouHearQuestionResponse> getTypeWhatYouHearQuestionById(Long id) {
        String sql = """
                select q.id            as id,
                       q.title         as title,
                       q.duration      as duration,
                       q.statement     as statement,
                       q.question_type as questionType,
                       f.file_url      as fileUrl,
                       f.file_type     as fileType,
                       q.correct_answer as correctAnswer,
                       t.id as testId
                from questions q
                         join tests t on t.id = q.test_id
                         join files f on q.id = f.question_id
                where q.question_type = 'TYPE_WHAT_YOU_HEAR' and q.id = ?
                """;
        return jdbcTemplate.query(sql, (resultSet, i) -> new TypeWhatYouHearQuestionResponse(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getInt("duration"),
                QuestionType.valueOf(resultSet.getString("questionType")),
                resultSet.getLong("id"),
                resultSet.getLong("fileId"),
                FileType.valueOf(resultSet.getString("fileType")),
                resultSet.getString("fileUrl"),
                resultSet.getString("correctAnswer"),
                resultSet.getLong("testId")), id).stream().findAny();
    }
}
