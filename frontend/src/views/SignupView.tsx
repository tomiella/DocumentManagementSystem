import React from "react";
import { useSignupVM } from "../viewmodels/useSignupVM";

export default function SignupView() {
  const vm = useSignupVM();

  return (
    <div className="max-w-md mx-auto mt-20 bg-bg border rounded p-6 space-y-4">
      <h2 className="text-xl font-semibold">Sign up</h2>

      <form onSubmit={vm.onSubmit} noValidate className="space-y-3">
        <div className="flex flex-col gap-1">
          <label htmlFor="username" className="text-sm font-medium">User Name</label>
          <input
            id="username"
            name="username"
            className="border rounded px-3 py-2 w-full"
            placeholder="Insert user name"
            autoComplete="username"
            value={vm.username}
            onChange={vm.onUsername}
            required
          />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="email" className="text-sm font-medium">Email</label>
          <input
            id="email"
            name="email"
            type="email"
            className="border rounded px-3 py-2 w-full"
            placeholder="Insert email"
            autoComplete="email"
            value={vm.email}
            onChange={vm.onEmail}
            required
          />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="password" className="text-sm font-medium">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            className="border rounded px-3 py-2 w-full"
            placeholder="Insert password"
            autoComplete="new-password"
            minLength={8}
            value={vm.password}
            onChange={vm.onPassword}
            required
          />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="confirm" className="text-sm font-medium">Confirm Password</label>
          <input
            id="confirm"
            name="confirm"
            type="password"
            className="border rounded px-3 py-2 w-full"
            placeholder="Re-insert password"
            autoComplete="new-password"
            minLength={8}
            value={vm.confirm}
            onChange={vm.onConfirm}
            required
          />
        </div>

        {vm.error && (
          <div className="text-red-700 text-sm" role="alert" aria-live="assertive">{vm.error}</div>
        )}

        <button
          type="submit"
          className="bg-green-900 text-white rounded px-3 py-2 w-full disabled:opacity-50"
          disabled={vm.submitting}
        >
          {vm.submitting ? 'Signing up…' : 'Sign up'}
        </button>
      </form>
    </div>
  );
}