import api from './axios';

export const getSettings = () =>
  api.get('/api/settings', {
    params: { _t: Date.now() },
    headers: {
      'Cache-Control': 'no-cache',
      Pragma: 'no-cache',
      Expires: '0',
    },
  });

export const updateSettings = (data) => api.put('/api/settings', data);
