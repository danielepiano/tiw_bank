## About the project
<i>TIW Bank</i> is a simple web platform for managing money transfers between different accounts, holded by registered users.

Developed as the final project for the teaching of <i>Web Technologies</i> for the bachelor degree in <i>Computer Engineering</i> at the Politecnico di Milano, A.A. 2021-2022.


## Adopted technologies
- JavaEE (Servlet, JSP, JSTL, Filter, ...)
- Apache Tomcat
- MySQL server


## Related schemas
The application relies on three simple tables, defined in the MySQL DDL as follows:

```
CREATE TABLE `users` (
    `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `first_name` varchar(45) NOT NULL,
    `last_name` varchar(45) NOT NULL,
    `role` enum('admin','customer') NOT NULL,
    `email` varchar(320) NOT NULL,
    `password` varchar(32) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `email_UNIQUE` (`email`)
);

CREATE TABLE `current_accounts` (
    `id` int(10) unsigned NOT NULL AUTO_INCREMENT, 
    `account_number` varchar(12) NOT NULL, 
    `balance` float NOT NULL DEFAULT '0',
    `holder_id` int(10) unsigned NOT NULL, 
    PRIMARY KEY (`id`),
    UNIQUE KEY `account_number_UNIQUE` (`account_number`),
    INDEX `holder_id_idx` (`holder_id`),
    CONSTRAINT `holder_id`
        FOREIGN KEY (`holder_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `transfers` (
    `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `amount` FLOAT NOT NULL,
    `reason` VARCHAR(140) NOT NULL,
    `issue_date` DATE NOT NULL,
    `sender_account_id` INT(10) UNSIGNED NOT NULL,
    `recipient_account_id` INT(10) UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `sender_account_id_idx` (`sender_account_id` ASC),
    INDEX `recipient_account_id_idx` (`recipient_account_id` ASC),
    CONSTRAINT `sender_account_id`
        FOREIGN KEY (`sender_account_id`) REFERENCES `tiw_bank`.`current_accounts` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `recipient_account_id`
        FOREIGN KEY (`recipient_account_id`) REFERENCES `tiw_bank`.`current_accounts` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
);
```
