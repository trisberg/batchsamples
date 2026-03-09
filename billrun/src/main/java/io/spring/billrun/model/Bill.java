package io.spring.billrun.model;

public record Bill(Long id, String firstName, String lastName, Long dataUsage, Long minutes, Double billAmount) {
}
