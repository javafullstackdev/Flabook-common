package com.msquare.flabook.repository;

import com.msquare.flabook.enumeration.OsType;
import com.msquare.flabook.models.ReleaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * APP 릴리즈 히스토리 Repository
 */
@Repository
public interface ReleaseNoteRepository  extends JpaRepository<ReleaseNote, Long> {

	List<ReleaseNote> findByOsTypeOrderByReleaseDateDesc(OsType osType);

}
