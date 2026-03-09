package io.spring.billrun.model;

public record Usage(Long id, String firstName, String lastName, Long minutes, Long dataUsage) {
}
