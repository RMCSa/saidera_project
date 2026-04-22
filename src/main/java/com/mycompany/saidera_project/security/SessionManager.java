package com.mycompany.saidera_project.security;

import com.mycompany.saidera_project.models.User;

/**
 * Singleton responsável por gerenciar a sessão do usuário atualmente logado.
 */
public class SessionManager {
    /** Instância única do SessionManager. */
    private static SessionManager instance;
    /** Referência para o usuário atualmente logado no sistema. */
    private User currentUser;

    /** Construtor privado para evitar instanciação direta. */
    private SessionManager() {}

    /**
     * Obtém a instância global do gerenciador de sessão.
     * 
     * @return A instância única do SessionManager.
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Inicia uma sessão com o usuário especificado.
     * 
     * @param user O usuário que foi autenticado.
     */
    public void login(User user) {
        this.currentUser = user;
    }

    /**
     * Encerra a sessão atual, limpando a referência do usuário.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Retorna o usuário logado nesta sessão.
     * 
     * @return O objeto User atual ou null se ninguém estiver logado.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Verifica se existe alguma sessão ativa/logada no sistema.
     * 
     * @return True se um usuário estiver autenticado, false caso contrário.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
