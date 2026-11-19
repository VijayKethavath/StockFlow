import api from './axios';

export const login = (data) => api.post('/api/auth/login', data);

export const signup = (data) => api.post('/api/auth/signup', data);
