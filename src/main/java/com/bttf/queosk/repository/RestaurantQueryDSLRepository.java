package com.bttf.queosk.repository;

import com.bttf.queosk.entity.QRestaurant;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.enumerate.RestaurantCategory;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

import static com.querydsl.core.types.dsl.MathExpressions.*;

@Repository
@RequiredArgsConstructor
public class RestaurantQueryDSLRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final QRestaurant qRestaurant = QRestaurant.restaurant;
    private static final String ALL_CATEGORIES = "ALL";

    public Page<Restaurant> getRestaurantListByDistance(
            double lng, double lat, Pageable pageable, String category, String keyword) {
        JPAQuery<Restaurant> query = queryFactory.selectFrom(qRestaurant);

        NumberExpression<Double> distance = calculateDistance(lat, lng);

        addCategoryFilter(query, category);
        addKeywordFilter(query, keyword);
        addDistanceOrdering(query, distance, pageable);

        List<Restaurant> results = query.fetch();
        long totalCount = query.fetchCount();

        return new PageImpl<>(results, pageable, totalCount);
    }

    private NumberExpression<Double> calculateDistance(double lat, double lng) {
        NumberExpression<Double> latExpression = Expressions.numberTemplate(Double.class, String.valueOf(lat));
        NumberExpression<Double> lngExpression = Expressions.numberTemplate(Double.class, String.valueOf(lng));
        NumberExpression<Double> cosLat = cos(radians(latExpression));
        NumberExpression<Double> cosLng = cos(radians(lngExpression));
        NumberExpression<Double> sinLat = sin(radians(latExpression));
        NumberExpression<Double> sinLng = sin(radians(lngExpression));

        NumberExpression<Double> acosValue = cosLat
                .multiply(cos(qRestaurant.latitude))
                .multiply(cos(qRestaurant.longitude.subtract(lng)))
                .add(sinLat.multiply(sin(qRestaurant.latitude)));

        return acosValue
                .multiply(Expressions.numberTemplate(Double.class, "6371")) // 지구 반지름 (km)
                .doubleValue(); // double 형식으로 변환
    }

    private void addCategoryFilter(JPAQuery<Restaurant> query, String category) {
        if (!ALL_CATEGORIES.equals(category)) {
            query.where(qRestaurant.category.eq(RestaurantCategory.valueOf(category)));
        }
    }

    private void addKeywordFilter(JPAQuery<Restaurant> query, String keyword) {
        if (Objects.nonNull(keyword) && !keyword.isEmpty()) {
            query.where(qRestaurant.restaurantName.contains(keyword));
        }
    }

    private void addDistanceOrdering(JPAQuery<Restaurant> query, NumberExpression<Double> distance, Pageable pageable) {
        query.where(distance.isNotNull()) // 거리 값이 null이 아닌 경우 필터링
                .orderBy(distance.asc()) // 거리에 대한 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
    }
}

