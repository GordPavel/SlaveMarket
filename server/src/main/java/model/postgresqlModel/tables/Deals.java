/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package model.postgresqlModel.tables;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "deals")
public class Deals {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deals_seq")
    @SequenceGenerator(
            name = "deals_seq",
            sequenceName = "deals_id_seq",
            allocationSize = 1
    )
    @Column
    private int id;
    @Column
    private int userId;
    @Column
    private String state;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @Column
    private int merchId;
    @Column
    private int price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getMerchId() {
        return merchId;
    }

    public void setMerchId(int merchId) {
        this.merchId = merchId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Deals{" +
                "id=" + id +
                ", userId=" + userId +
                ", state='" + state + '\'' +
                ", time=" + time +
                ", merchId=" + merchId +
                ", price=" + price +
                '}';
    }
}
