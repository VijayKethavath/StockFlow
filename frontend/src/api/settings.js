import api from './axios';

export const getSettings = () =>
  api.get('/api/settings', {
    params: { _t: Date.now() },
  });

export const updateSettings = (data) => api.put('/api/settings', data);
