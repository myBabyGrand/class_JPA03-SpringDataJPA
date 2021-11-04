package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity
        implements Persistable<Long> {
    @Id
//    @GeneratedValue
    @Column(name= "ITEM_ID")
    private Long id;

    private String itemName;

    public Item(Long id, String itemName) {
        this.id = id;
        this.itemName = itemName;
    }

    public Item(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public boolean isNew() {//select 없이 판단한다.
        return this.getCreatedDate() == null;
    }
}
