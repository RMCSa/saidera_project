package com.mycompany.saidera_project.models;

/**
 * Entidade que armazena os dados, papel funcional e credenciais
 * de um funcionário que acessa o sistema.
 */
public class User {
    /** Identificador único do usuário. */
    private String id;
    /** Nome completo do usuário. */
    private String name;
    /** E-mail associado (utilizado para login). */
    private String email;
    /** Cargo ou função exercida (ex: Gerente, Garçom). */
    private String role;
    /** Data que ocorreu o registro no sistema de folha. */
    private String registrationDate;
    /** Senha não codificada de acesso (para fins de mock/projeto didático). */
    private String password;

    /**
     * Construtor para atribuir informações do Usuário.
     * 
     * @param id ID unívoco do usuário
     * @param name Nome exibido na interface
     * @param email Email de logon
     * @param role Cargo no sistema
     * @param registrationDate String da data de registro
     * @param password Credencial de senha
     */
    public User(String id, String name, String email, String role, String registrationDate, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.registrationDate = registrationDate;
        this.password = password;
    }

    // Getters and Setters
    /** @return O identificador do usuário */
    public String getId() { return id; }
    /** @return O nome do usuário */
    public String getName() { return name; }
    /** @return O endereço de email usado para login */
    public String getEmail() { return email; }
    /** @return O cargo (função de permissões) */
    public String getRole() { return role; }
    /** @return A listagem de data como texto */
    public String getRegistrationDate() { return registrationDate; }
    /** @return A string real do campo de senha do acesso */
    public String getPassword() { return password; }
}
