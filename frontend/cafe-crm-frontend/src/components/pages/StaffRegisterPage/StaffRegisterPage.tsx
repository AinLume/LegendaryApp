import { useState, type FC } from 'react';
import { authApi } from '../../../api/auth';
import { Input, Select, Button } from '../../../components/ui';
import { StaffRole, StaffRoleLabels } from '../../../types';

interface FormErrors {
  name?: string;
  email?: string;
  phone?: string;
  password?: string;
  role?: string;
  general?: string;
}

interface FormTouched {
  name: boolean;
  email: boolean;
  phone: boolean;
  password: boolean;
  role: boolean;
}

export const StaffRegisterPage: FC = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    role: StaffRole.WAITER,
  });
  const [touched, setTouched] = useState<FormTouched>({
    name: false,
    email: false,
    phone: false,
    password: false,
    role: false,
  });
  const [errors, setErrors] = useState<FormErrors>({});
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const validateField = (name: string, value: string): string | undefined => {
    switch (name) {
      case 'name':
        if (!value.trim()) return 'Имя обязательно';
        if (value.trim().length < 2) return 'Имя должно содержать минимум 2 символа';
        break;
      case 'email':
        if (!value.trim()) return 'Email обязателен';
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) return 'Некорректный email';
        break;
      case 'phone':
        if (!value.trim()) return 'Телефон обязателен';
        const phoneRegex = /^\+?[0-9]{10,15}$/;
        if (!phoneRegex.test(value.replace(/[\s()-]/g, ''))) {
          return 'Некорректный номер телефона';
        }
        break;
      case 'password':
        if (!value) return 'Пароль обязателен';
        if (value.length < 6) return 'Пароль должен содержать минимум 6 символов';
        break;
      case 'role':
        if (!value) return 'Роль обязательна';
        break;
    }
    return undefined;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));

    if (touched[name as keyof FormTouched]) {
      const error = validateField(name, value);
      setErrors((prev) => ({ ...prev, [name]: error, general: undefined }));
    }
  };

  const handleBlur = (e: React.FocusEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setTouched((prev) => ({ ...prev, [name]: true }));
    const error = validateField(name, value);
    setErrors((prev) => ({ ...prev, [name]: error }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const newTouched = { name: true, email: true, phone: true, password: true, role: true };
    setTouched(newTouched);

    const newErrors: FormErrors = {};
    let hasError = false;

    Object.entries(formData).forEach(([key, value]) => {
      const error = validateField(key, value as string);
      if (error) {
        newErrors[key as keyof FormErrors] = error;
        hasError = true;
      }
    });

    if (hasError) {
      setErrors(newErrors);
      return;
    }

    setLoading(true);
    setErrors({});

    try {
      await authApi.registerStaff(formData);
      setSuccess(true);
      setFormData({
        name: '',
        email: '',
        phone: '',
        password: '',
        role: StaffRole.WAITER,
      });
      setTouched({ name: false, email: false, phone: false, password: false, role: false });
    } catch (err) {
      setErrors({
        general: err instanceof Error ? err.message : 'Ошибка при регистрации сотрудника',
      });
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="max-w-2xl mx-auto p-6">
        <div className="bg-green-50 border border-green-200 rounded-xl p-8 text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h2 className="text-xl font-semibold text-green-800 mb-2">Сотрудник успешно зарегистрирован!</h2>
          <p className="text-green-700 mb-6">
            {formData.name || 'Новый сотрудник'} добавлен в систему с ролью {StaffRoleLabels[formData.role]}
          </p>
          <Button
            onClick={() => setSuccess(false)}
            variant="primary"
          >
            Зарегистрировать еще одного
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Регистрация сотрудника</h1>
        <p className="text-gray-600 mt-1">Заполните данные нового сотрудника системы</p>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <form onSubmit={handleSubmit} className="space-y-5">
          {errors.general && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
              {errors.general}
            </div>
          )}

          <Input
            id="name"
            name="name"
            label="Полное имя"
            placeholder="Иван Иванов"
            value={formData.name}
            onChange={handleChange}
            onBlur={handleBlur}
            error={touched.name ? errors.name : undefined}
            autoComplete="name"
          />

          <Input
            id="email"
            name="email"
            type="email"
            label="Email"
            placeholder="ivan@example.com"
            value={formData.email}
            onChange={handleChange}
            onBlur={handleBlur}
            error={touched.email ? errors.email : undefined}
            autoComplete="email"
          />

          <Input
            id="phone"
            name="phone"
            type="tel"
            label="Телефон"
            placeholder="+79001234567"
            value={formData.phone}
            onChange={handleChange}
            onBlur={handleBlur}
            error={touched.phone ? errors.phone : undefined}
            autoComplete="tel"
          />

          <Input
            id="password"
            name="password"
            type="password"
            label="Пароль"
            placeholder="Минимум 6 символов"
            value={formData.password}
            onChange={handleChange}
            onBlur={handleBlur}
            error={touched.password ? errors.password : undefined}
            autoComplete="new-password"
          />

          <Select
            id="role"
            name="role"
            label="Роль"
            value={formData.role}
            onChange={handleChange}
            onBlur={handleBlur}
            error={touched.role ? errors.role : undefined}
          >
            {Object.entries(StaffRoleLabels).map(([key, label]) => (
              <option key={key} value={key}>
                {label}
              </option>
            ))}
          </Select>

          <div className="pt-4">
            <Button
              type="submit"
              disabled={loading}
              className="w-full"
              variant="primary"
            >
              {loading ? 'Регистрация...' : 'Зарегистрировать сотрудника'}
            </Button>
          </div>
        </form>

        <div className="mt-6 pt-6 border-t border-gray-100">
          <h3 className="text-sm font-medium text-gray-700 mb-3">Информация о ролях:</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm">
            <div className="flex items-start gap-2">
              <span className="text-primary font-semibold">ADMIN</span>
              <span className="text-gray-600">— полный доступ ко всем функциям</span>
            </div>
            <div className="flex items-start gap-2">
              <span className="text-primary font-semibold">WAITER</span>
              <span className="text-gray-600">— управление заказами и столами</span>
            </div>
            <div className="flex items-start gap-2">
              <span className="text-primary font-semibold">COOK</span>
              <span className="text-gray-600">— управление кухней и меню</span>
            </div>
            <div className="flex items-start gap-2">
              <span className="text-primary font-semibold">BARTENDER</span>
              <span className="text-gray-600">— управление баром и меню</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
