// placement: E - Main Content

import { useState } from 'react';
export default function Signin() {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    const fd = new FormData(e.currentTarget);
    const username = String(fd.get('username') || '').trim();
    const password = String(fd.get('password') || '');
    const confirm = String(fd.get('confirm') || '');

    if (!username) return setError('Username is required.');
    if (password && password.length < 8) return setError('Password must be at least 8 characters.');
    if (password !== confirm) return setError('Passwords do not match.');

    setSubmitting(true);
    try {
      // TODO: call api.ts -> updateProfile({ username, password })
      // await updateProfile({ username, password });
      setSuccess('Profile updated successfully.');
      (e.currentTarget as HTMLFormElement).reset();
    } catch (err) {
      setError('Update failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="max-w-md mx-auto mt-20 bg-bg border rounded p-6 space-y-4">
      <h2 className="text-xl font-semibold">Profile Update</h2>

      <form onSubmit={onSubmit} noValidate className="space-y-3">
        <div className="flex flex-col gap-1">
          <label htmlFor="username" className="text-sm font-medium">User Name</label>
          <input
            id="username"
            name="username"
            className="border rounded px-3 py-2 w-full"
            placeholder="User Name"
            autoComplete="username"
            required
          />
        </div>

          <h2 className="text-xl font-semibold">Reset Password</h2>
        <div className="flex flex-col gap-1">
          <label htmlFor="password" className="text-sm font-medium">New Password</label>
          <input
            id="password"
            name="password"
            type="password"
            className="border rounded px-3 py-2 w-full"
            placeholder="Enter new password"
            autoComplete="new-password"
            minLength={8}
          />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="confirm" className="text-sm font-medium">Confirm Password</label>
          <input
            id="confirm"
            name="confirm"
            type="password"
            className="border rounded px-3 py-2 w-full"
            placeholder="Re-enter new password"
            autoComplete="new-password"
            minLength={8}
          />
        </div>

        {error && (
          <div className="text-red-700 text-sm" role="alert" aria-live="assertive">{error}</div>
        )}
        {success && (
          <div className="text-green-700 text-sm" role="status" aria-live="polite">{success}</div>
        )}

        <button
          type="submit"
          // className="border rounded px-3 py-2 w-full disabled:opacity-50"
          className="
              border-e-emerald-700
              border-b-emerald-600
              border-t-emerald-200 border-2
              border-l-emerald-200
              rounded px-3 py-2
              text-green-600 text-sm
              w-full"
          disabled={submitting}
        >
          {submitting ? 'Updating…' : 'Update'}
        </button>
      </form>
    </div>
  );
}