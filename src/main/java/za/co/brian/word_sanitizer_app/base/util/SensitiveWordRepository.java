package za.co.brian.word_sanitizer_app.base.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.brian.word_sanitizer_app.base.sensitive_word.SensitiveWord;

import java.util.List;


public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long> {

    Page<SensitiveWord> findAllById(Long id, Pageable pageable);

    boolean existsByWordIgnoreCase(String word);

    @Query("select w.word from SensitiveWord w where w.isActive = true")
    List<String> findAllActiveWords();

}
