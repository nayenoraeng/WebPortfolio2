package com.project.parkrental.parkList.repository;

import com.project.parkrental.parkList.model.ParkList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkListRepository extends JpaRepository<ParkList, Long> {

    // 공원 이름, 위도, 경도를 기반으로 중복 공원 확인 (데이터베이스에서 해당 공원이 존재하는지 검사)
    Optional<ParkList> findByParkNmAndLatitudeAndLongitude(String parkNm, Double latitude, Double longitude);

    // 위치와 카테고리(공원 구분)를 기반으로 반경 내 공원 목록을 가져옴
    // Haversine 공식을 이용해 공원의 위도, 경도와 사용자의 위치를 비교하여 반경 내에 있는 공원 검색
    @Query("SELECT p FROM ParkList p WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude)))) < :radius AND p.parkSe IN :parkSeList")
    List<ParkList> findByLocationAndParkSeIn(@Param("latitude") Double latitude,
                                             @Param("longitude") Double longitude,
                                             @Param("radius") Double radius,
                                             @Param("parkSeList") List<String> parkSeList);

    // 주어진 카테고리(공원 구분) 목록에 해당하는 공원들을 페이징 처리하여 반환
    Page<ParkList> findByParkSeIn(List<String> parkSeList, Pageable pageable);

    // 카테고리(공원 구분)와 키워드를 동시에 적용하여 공원을 검색 (페이징 처리)
    // 공원 이름 또는 주소가 키워드와 일치하는 데이터를 반환
    @Query("SELECT p FROM ParkList p WHERE p.parkSe IN :parkSeList AND (p.parkNm LIKE %:keyword% OR p.lnmadr LIKE %:keyword%)")
    Page<ParkList> findByCategoryAndKeyword(@Param("parkSeList") List<String> parkSeList,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);

    // 공원 이름이나 주소에 키워드가 포함된 공원 목록을 페이징 처리하여 반환
    Page<ParkList> findByParkNmContainingOrLnmadrContaining(String parkNm, String lnmadr, Pageable pageable);
}
