package com.mockcompany.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mockcompany.webapp.api.SearchReportResponse;
import com.mockcompany.webapp.data.ProductItemRepository;
import com.mockcompany.webapp.model.ProductItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import java.util.HashMap;

@Service
public class SearchService {

    @Autowired
    private ProductItemRepository productItemRepository;

    @Autowired
    private EntityManager entityManager;

    public List<ProductItem> searchProduct(String query){
        Iterable<ProductItem> allItems = this.productItemRepository.findAll();
        List<ProductItem> itemList = new ArrayList<>();

        boolean exactMatch = query.startsWith("\"") && query.endsWith("\"");
        if(exactMatch){
            query = query.substring(1,query.length()-1);
        }

        // This is a loop that the code inside will execute on each of the items from the database.
        for (ProductItem item : allItems) {
            // TODO: Figure out if the item should be returned based on the query parameter!
            boolean matchesSearch = false;

            if(exactMatch){
                if(item.getName().contains(query) || item.getDescription().contains(query)){
                    matchesSearch = true;
                }
            }
            else{
                if(item.getName().toLowerCase().contains(query.toLowerCase()) || item.getDescription().toLowerCase().contains(query.toLowerCase())){
                matchesSearch = true;
                }

            }
            if(matchesSearch){
                itemList.add(item);
            }
        }
            return itemList;
    }
    
    public SearchReportResponse runReport(){
        Map<String, Integer> hits = new HashMap<>();
        SearchReportResponse response = new SearchReportResponse();
        response.setSearchTermHits(hits);

        int count = this.entityManager.createQuery("SELECT item FROM ProductItem item").getResultList().size();

        List<Number> matchingIds = new ArrayList<>();
        matchingIds.addAll(
                this.entityManager.createQuery("SELECT item.id from ProductItem item where item.name like '%cool%'").getResultList()
        );
        matchingIds.addAll(
                this.entityManager.createQuery("SELECT item.id from ProductItem item where item.description like '%cool%'").getResultList()
        );
        matchingIds.addAll(
                this.entityManager.createQuery("SELECT item.id from ProductItem item where item.name like '%Cool%'").getResultList()
        );
        matchingIds.addAll(
                this.entityManager.createQuery("SELECT item.id from ProductItem item where item.description like '%cool%'").getResultList()
        );
        List<Number> counted = new ArrayList<>();
        for (Number id: matchingIds) {
            if (!counted.contains(id)) {
                counted.add(id);
            }
        }

        response.getSearchTermHits().put("Cool", counted.size());


        response.setProductCount(count);

        List<ProductItem> allItems = entityManager.createQuery("SELECT item FROM ProductItem item").getResultList();
        int kidCount = 0;
        int perfectCount = 0;
        Pattern kidPattern = Pattern.compile("(.*)[kK][iI][dD][sS](.*)");
        for (ProductItem item : allItems) {
            if (kidPattern.matcher(item.getName()).matches() || kidPattern.matcher(item.getDescription()).matches()) {
                kidCount += 1;
            }
            if (item.getName().toLowerCase().contains("perfect") || item.getDescription().toLowerCase().contains("perfect")) {
                perfectCount += 1;
            }
        }
        response.getSearchTermHits().put("Kids", kidCount);

        response.getSearchTermHits().put("Amazing", entityManager.createQuery("SELECT item FROM ProductItem item where lower(concat(item.name, ' - ', item.description)) like '%amazing%'").getResultList().size());

        hits.put("Perfect", perfectCount);

        return response;
    }
    
}
