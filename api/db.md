## mstuser

| Item name | Key | Type | Length | Necessary |
|---|---|---|---|---|
| psn_cd | PK | integer | 4 | x |
| user_id | | character | 8 | |
| username | | character | 8 | |
| password | | character | 255 | |
| role | | smallint | 2 | |
| status | | bit | 1 | |
| deletetime | | timestamp | | |
| createtime | | timestamp | | |
| create_psn_cd | FK | integer | 4 | |
| updatetime | | timestamp | | |
| update_psn_cd | FK | integer | 4 | |

## mstproducttype

| Item name | Key | Type | Length | Necessary |
|---|---|---|---|---|
| product_type_id | PK | varchar | 50 | x |
| name | | varchar | 200 | |
| status | | boolean | 1 | |
| createtime | | timestamp | | |
| create_user |  | varchar | 50 | |
| updatetime | | timestamp | | |
| update_user |  | varchar | 50 | |

## mstproduct

| Item name | Key | Type | Length | Necessary |
|---|---|---|---|---|
| product_id | PK | varchar | 50 | x |
| product_name | | varchar | 200 | |
| status | | boolean | 1 | |
| description | | varchar | 500 | |
| product_img | | varchar | 500 | |
| product_amount | | integer | 4 | |
| price | | bigint | 8 | |
| product_type_id | FK | varchar | 50 | |
| createtime | | timestamp | | |
| create_user |  | character | 8 | |
| updatetime | | timestamp | | |
| update_user |  | character | 8 | |

## trproductorder

| Item name | Key | Type | Length | Necessary |
|---|---|---|---|---|
| id | PK | bigint | 8 | x |
| custom_name | | varchar | 200 | |
| order_product_id | FK | bigint | 8 | |
| order_product_amount | | integer | 4 | |
| unit_price | | bigint | 8 | |
| total_price | | bigint | 8 | |
| order_status | | varchar | 50 | |
| order_delivery_address | | varchar | 400 | |
| order_delivery_date | | timestamp | | |
| createtime | | timestamp | | |
| create_user | FK | integer | 4 | |
| updatetime | | timestamp | | |
| update_user | FK | integer | 4 | |




- cho nhap tu file excel