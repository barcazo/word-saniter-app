package za.co.brian.word_sanitizer_app.base.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import za.co.brian.word_sanitizer_app.base.sensitive_word.SensitiveWord;
import za.co.brian.word_sanitizer_app.base.sensitive_word.SensitiveWordDTO;
import za.co.brian.word_sanitizer_app.base.sensitive_word.SensitiveWordMapper;


@Service
public class SensitiveWordService {

    private final SensitiveWordRepository sensitiveWordRepository;
    private final SensitiveWordMapper sensitiveWordMapper;

    public SensitiveWordService(final SensitiveWordRepository sensitiveWordRepository,
                                    final SensitiveWordMapper sensitiveWordMapper) {
        this.sensitiveWordRepository = sensitiveWordRepository;
        this.sensitiveWordMapper = sensitiveWordMapper;
    }

    public Page<SensitiveWordDTO> findAll(final String filter, final Pageable pageable) {
        Page<SensitiveWord> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = sensitiveWordRepository.findAllById(longFilter, pageable);
        } else {
            page = sensitiveWordRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(sensitiveWord -> sensitiveWordMapper.updateSensitiveWordDTO(sensitiveWord, new SensitiveWordDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public SensitiveWordDTO get(final Long id) {
        return sensitiveWordRepository.findById(id)
                .map(sensitiveWord -> sensitiveWordMapper.updateSensitiveWordDTO(sensitiveWord, new SensitiveWordDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SensitiveWordDTO sensitiveWordDTO) {
        final SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWordMapper.updateSensitiveWord(sensitiveWordDTO, sensitiveWord);
        return sensitiveWordRepository.save(sensitiveWord).getId();
    }

    public void update(final Long id, final SensitiveWordDTO sensitiveWordDTO) {
        final SensitiveWord sensitiveWord = sensitiveWordRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        sensitiveWordMapper.updateSensitiveWord(sensitiveWordDTO, sensitiveWord);
        sensitiveWordRepository.save(sensitiveWord);
    }

    public void delete(final Long id) {
        final SensitiveWord sensitiveWord = sensitiveWordRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        sensitiveWordRepository.delete(sensitiveWord);
    }

    public boolean wordExists(final String word) {
        return sensitiveWordRepository.existsByWordIgnoreCase(word);
    }

}
