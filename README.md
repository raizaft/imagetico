# Retrato - Rede Social de Fotógrafos

Retrato é uma aplicação web que permite aos fotógrafos registrar suas fotos em uma rede social. Os usuários podem curtir, comentar e seguir outros fotógrafos, interagindo com as fotos e promovendo o conteúdo através de hashtags. O administrador tem a responsabilidade de moderar os fotógrafos e garantir que todos sigam as regras da rede. Caso contrário, o fotógrafo poderá ser bloqueado temporariamente ou permanentemente.

## Funcionalidades

- **Cadastro de Fotógrafos**: Fotógrafos podem se cadastrar na rede e registrar suas fotos.
- **Curtidas e Comentários**: Todos os usuários podem curtir e comentar nas fotos, com a possibilidade de usar hashtags.
- **Seguir e Ser Seguido**: Fotógrafos podem seguir uns aos outros para acompanhar as postagens.
- **Bloqueio de Usuários**: Administradores podem bloquear temporariamente ou permanentemente fotógrafos que não seguirem as regras da rede.
- **Administração**: Administradores podem moderar o conteúdo e gerenciar os usuários.
- **Paginação**: A visualização de fotos e comentários é paginada para melhorar a performance e a usabilidade.

## Tecnologias Utilizadas

- **Backend**:
  - **Spring Boot** (para a implementação do backend)
  - **Spring MVC** (para controle das rotas e páginas)
  - **Hibernate** (para persistência de dados)
  - **PostgreSQL** (banco de dados relacional)
- **Frontend**:
  - **Thymeleaf** (para templates dinâmicos)
  - **Bootstrap** (para o design responsivo)
- **Outras Ferramentas**:
  - **Maven** (para gerenciamento de dependências e build)

## Como Rodar o Projeto

### Pré-requisitos

Certifique-se de que você tem as seguintes ferramentas instaladas:

- [Java JDK 11 ou superior](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven](https://maven.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)
