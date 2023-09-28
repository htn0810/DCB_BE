package com.bosch.digicore.clients.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class OrgReq {

    private int TypeId;

    private List<Item> Items;

    public OrgReq(String orgName) {
        TypeId = 1;
        Items = Arrays.asList(
                new Item(2695, orgName), new Item(2697, orgName),
                new Item(2699, orgName), new Item(2701, orgName)
        );
    }

    @Getter
    @Setter
    static class Item {

        private boolean IsAnd;

        private int OperatorId;

        private int SelectedPropertyId;

        private String Type;

        private String Value;

        public Item(int selectedPropertyId, String value) {
            IsAnd = false;
            OperatorId = 30;
            SelectedPropertyId = selectedPropertyId;
            Type = "Clause";
            Value = value;
        }
    }
}
