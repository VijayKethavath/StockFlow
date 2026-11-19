import { useState, useEffect } from 'react';
import { getSettings, updateSettings } from '../api/settings';
import LoadingSpinner from '../components/LoadingSpinner';
import { HiOutlineCheck, HiOutlineCog, HiOutlineInformationCircle } from 'react-icons/hi';

export default function Settings() {
  const [threshold, setThreshold] = useState(10);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    const fetchSettings = async () => {
      try {
        const response = await getSettings();
        setThreshold(response.data.defaultLowStockThreshold ?? 10);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load settings');
      } finally {
        setLoading(false);
      }
    };
    fetchSettings();
  }, []);

  useEffect(() => {
    if (success) {
      const timer = setTimeout(() => setSuccess(false), 3500);
      return () => clearTimeout(timer);
    }
  }, [success]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSaving(true);

    try {
      const parsed = parseInt(threshold, 10);
      await updateSettings({ defaultLowStockThreshold: isNaN(parsed) ? 10 : parsed });
      setSuccess(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update settings');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="max-w-3xl mx-auto space-y-6 animate-in fade-in duration-200">
      <div>
        <h2 className="text-xl sm:text-2xl font-bold text-gray-900 tracking-tight flex items-center gap-2.5">
          <HiOutlineCog className="w-7 h-7 text-blue-600" />
          <span>Organization Settings</span>
        </h2>
        <p className="text-xs sm:text-sm text-gray-500 mt-0.5">
          Configure default thresholds and global preferences for your inventory system
        </p>
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 text-red-700 text-sm rounded-xl">
          {error}
        </div>
      )}

      {success && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm rounded-xl flex items-center gap-2 font-medium">
          <HiOutlineCheck className="w-5 h-5 text-emerald-600 flex-shrink-0" />
          <span>Organization settings saved successfully.</span>
        </div>
      )}

      <div className="bg-white rounded-2xl shadow-xs border border-gray-200/80 p-6 sm:p-8">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <h3 className="text-sm font-bold text-gray-900 uppercase tracking-wider">
              Inventory Alert Defaults
            </h3>
            <p className="text-xs text-gray-500 mt-1">
              Set the organization-wide baseline threshold for low-stock warnings.
            </p>
          </div>

          <div className="max-w-sm pt-4 border-t border-gray-100">
            <label
              htmlFor="threshold"
              className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-1.5"
            >
              Default Low-Stock Threshold (Units)
            </label>
            <input
              id="threshold"
              type="number"
              min="0"
              value={threshold}
              onChange={(e) => setThreshold(e.target.value)}
              required
              className="w-full px-4 py-2.5 bg-gray-50/50 border border-gray-200 rounded-xl text-sm font-bold text-gray-900 focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
            />
            <div className="mt-2.5 p-3 rounded-xl bg-blue-50/60 border border-blue-100/70 flex items-start gap-2 text-xs text-blue-700">
              <HiOutlineInformationCircle className="w-4 h-4 flex-shrink-0 mt-0.5" />
              <span>
                Any product that does not specify an individual custom threshold will automatically inherit this value.
              </span>
            </div>
          </div>

          <div className="pt-4 border-t border-gray-100">
            <button
              type="submit"
              disabled={saving}
              className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-semibold text-sm rounded-xl shadow-xs hover:shadow transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {saving ? 'Saving...' : 'Save Settings'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
