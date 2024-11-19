package com.project.parkrental.community;

import java.io.Serializable;
import java.util.Objects;

public class CommuLikeId implements Serializable {

    private String username;
    private Long community;  // Community의 idx 필드

    // 기본 생성자
    public CommuLikeId() {}

    // hashCode 및 equals 메서드 구현 (복합 키 비교를 위해 필요)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommuLikeId that = (CommuLikeId) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(community, that.community);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, community);
    }
}
