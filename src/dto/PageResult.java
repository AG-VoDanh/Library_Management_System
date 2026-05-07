package dto;

import java.util.List;

public record PageResult<T>(List<T> data, int total) {
}

