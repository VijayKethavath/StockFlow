import api from './axios';

export const getDashboard = () =>
  api.get('/api/dashboard', {
    params: { _t: Date.now() },
  });
