package com.bttf.queosk.batch;

import com.bttf.queosk.entity.Settlement;
import org.springframework.batch.item.ItemProcessor;

public class SettlementItemProcessor implements ItemProcessor<Object, Settlement> {
    // 쿼리 결과
    @Override
    public Settlement process(Object item) throws Exception {
        if (item instanceof Object[]) {
            Object[] result = (Object[]) item;

            // JPQL 쿼리 결과에서 필요한 값 추출
            String shopName = (String) result[0];
            Double totalMenuPrice = (Double) result[1];

            // Settlement 엔티티 생성 및 값 설정
            Settlement settlement = Settlement.builder()
                    .restaurantId(Long.parseLong(shopName))
                    .price(totalMenuPrice.longValue())
                    .build();

            return settlement;
        }
        return null; // 처리할 수 없는 데이터는 null을 반환하여 스킵하도록 처리할 수 있습니다.
    }
}
