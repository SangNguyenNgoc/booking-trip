package com.example.location;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class LocationApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocationApplication.class, args);
    }

//    @Bean
//    CommandLineRunner commandLineRunner(RegionRepository regionRepository) {
//        return args -> {
//            if (regionRepository.count() == 0) {
//                ObjectMapper mapper = new ObjectMapper();
//                TypeReference<List<Region>> typeReference = new TypeReference<List<Region>>() {};
//
//                InputStream inputStream = LocationApplication.class.getResourceAsStream("/data/regions.json");
//                try {
//                    List<Region> regions = mapper.readValue(inputStream, typeReference);
//
//                    regionRepository.saveAll(regions);
//
//                    System.out.println("Đã import " + regions.size() + " tỉnh/thành phố vào bảng region.");
//                } catch (Exception e) {
//                    System.out.println("Không thể import dữ liệu: " + e.getMessage());
//                }
//            } else {
//                System.out.println("Bảng region đã có dữ liệu. Bỏ qua việc import.");
//            }
//        };
//    }
}
