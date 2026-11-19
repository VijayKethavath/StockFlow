import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  HiOutlineChartBar,
  HiOutlineCube,
  HiOutlineCog,
  HiOutlineLogout,
  HiOutlineX,
  HiOutlineChevronLeft,
  HiOutlineChevronRight,
  HiOutlineOfficeBuilding,
} from 'react-icons/hi';

const navItems = [
  { to: '/dashboard', label: 'Dashboard', icon: HiOutlineChartBar },
  { to: '/products', label: 'Products', icon: HiOutlineCube },
  { to: '/settings', label: 'Settings', icon: HiOutlineCog },
];

export default function Sidebar({
  mobileOpen,
  onMobileClose,
  collapsed,
  onToggleCollapse,
}) {
  const { user, logout } = useAuth();

  return (
    <>
      {/* Mobile Backdrop */}
      {mobileOpen && (
        <div
          className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs z-40 md:hidden transition-opacity"
          onClick={onMobileClose}
          aria-hidden="true"
        />
      )}

      {/* Sidebar Container */}
      <aside
        className={`fixed top-0 bottom-0 left-0 z-40 flex flex-col bg-white border-r border-gray-200/90 transition-all duration-300 ease-in-out
          ${mobileOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}
          ${collapsed ? 'md:w-20' : 'w-64'}
        `}
      >
        {/* Brand Header */}
        <div className="flex items-center justify-between h-16 px-4 border-b border-gray-100">
          <div className="flex items-center gap-3 overflow-hidden">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center flex-shrink-0 shadow-sm shadow-blue-500/20 text-white font-bold">
              <HiOutlineCube className="w-6 h-6" />
            </div>
            {!collapsed && (
              <div className="flex flex-col min-w-0">
                <span className="text-lg font-bold text-gray-900 tracking-tight leading-tight">
                  StockFlow
                </span>
                <span className="text-[11px] font-medium text-blue-600 uppercase tracking-wider">
                  SaaS Inventory
                </span>
              </div>
            )}
          </div>

          {/* Close button on mobile */}
          <button
            type="button"
            onClick={onMobileClose}
            className="p-1.5 rounded-lg text-gray-500 hover:text-gray-900 hover:bg-gray-100 md:hidden"
            aria-label="Close sidebar"
          >
            <HiOutlineX className="w-5 h-5" />
          </button>
        </div>

        {/* Organization Pill (when expanded) */}
        {!collapsed && user?.organizationName && (
          <div className="mx-4 mt-4 p-2.5 rounded-xl bg-blue-50/60 border border-blue-100/80 flex items-center gap-2.5">
            <div className="w-7 h-7 rounded-lg bg-blue-600/10 text-blue-600 flex items-center justify-center flex-shrink-0">
              <HiOutlineOfficeBuilding className="w-4 h-4" />
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-[10px] uppercase font-semibold text-blue-600 tracking-wider">Tenant Org</p>
              <p className="text-xs font-bold text-gray-800 truncate">{user.organizationName}</p>
            </div>
          </div>
        )}

        {/* Navigation Items */}
        <nav className="flex-1 px-3 py-4 space-y-1.5 overflow-y-auto">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              onClick={onMobileClose}
              title={collapsed ? item.label : undefined}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all group ${
                  isActive
                    ? 'bg-blue-600 text-white shadow-sm shadow-blue-600/20'
                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100/80'
                } ${collapsed ? 'justify-center' : ''}`
              }
            >
              <item.icon className="w-5 h-5 flex-shrink-0" />
              {!collapsed && <span className="truncate">{item.label}</span>}
            </NavLink>
          ))}
        </nav>

        {/* Footer: Collapse button & Logout */}
        <div className="p-3 border-t border-gray-100 space-y-1">
          {/* Desktop collapse toggle */}
          <button
            type="button"
            onClick={onToggleCollapse}
            className={`hidden md:flex items-center gap-3 w-full px-3 py-2 text-xs font-medium text-gray-500 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors ${
              collapsed ? 'justify-center' : ''
            }`}
            title={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          >
            {collapsed ? (
              <HiOutlineChevronRight className="w-5 h-5" />
            ) : (
              <>
                <HiOutlineChevronLeft className="w-4 h-4" />
                <span>Collapse Sidebar</span>
              </>
            )}
          </button>

          {/* Logout button */}
          <button
            type="button"
            onClick={logout}
            className={`flex items-center gap-3 w-full px-3 py-2 text-sm font-medium text-red-600 hover:text-red-700 hover:bg-red-50/80 rounded-xl transition-colors ${
              collapsed ? 'justify-center' : ''
            }`}
            title={collapsed ? 'Sign Out' : undefined}
          >
            <HiOutlineLogout className="w-5 h-5 flex-shrink-0" />
            {!collapsed && <span className="truncate">Sign Out</span>}
          </button>
        </div>
      </aside>
    </>
  );
}
