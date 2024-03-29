# E-Commerce Microservices Project

Welcome to our E-Commerce Microservices project repository! This project aims to develop a scalable and modular e-commerce platform using microservices architecture.

## Collaborators

- [Hana Ben Amor](https://github.com/hanabenamor) - User Service
- [Iheb Ben Taher](https://github.com/ihebbentaher) - Inventory Service
- [Yassine Aloui](https://github.com/yassinealoui) - Order Service
- [Akrem Baccari](https://github.com/akrembaccari) - Product Service

## Description

Our project consists of building an e-commerce platform with a microservices architecture. Each microservice focuses on a specific domain, such as user management, inventory management, order processing, and product catalog management.

We aim to achieve the following goals with our project:
- Scalability: Easily scale each microservice independently based on demand.
- Flexibility: Modify and update different components of the system without affecting others.
- Resilience: Ensure high availability and fault tolerance through redundancy and failover mechanisms.
- Maintainability: Keep the codebase clean, modular, and well-documented to facilitate ongoing development and maintenance.

## Technologies Used

Our project utilizes the following technologies:

- **Spring Boot**: Framework for building microservices in Java.
- **Netflix Eureka**: Service discovery and registration.
- **Prometheus**: Monitoring and alerting toolkit.
- **Grafana**: Visualization and monitoring tool.
- **Docker**: Containerization platform for packaging and deploying microservices.

## Getting Started

To run the project locally, follow these steps:
1. Clone the repository: `git clone https://github.com/your-username/e-commerce-microservices.git`
2. Navigate to the project directory: `cd e-commerce-microservices`
3. Build the Docker images: `docker-compose build`
4. Start the containers: `docker-compose up`
5. Access the services via their respective endpoints.
