import { useEffect, useState } from "react";

import {
    getAdminUsers,
    createAdminUser,
    deactivateAdminUser,
} from "../../../api/adminUsersApi";

function Users() {

    const [users, setUsers] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [showCreateForm, setShowCreateForm] = useState(false);

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("OPERATOR");

    const [creating, setCreating] = useState(false);

    const fetchUsers = async () => {

        try {

            setLoading(true);
            setError("");

            const response = await getAdminUsers();

            setUsers(response.data || []);

        } catch (err) {

            console.error(
                "Admin kullanıcıları alınamadı:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Kullanıcılar yüklenirken bir hata oluştu."
            );

        } finally {

            setLoading(false);

        }
    };


    useEffect(() => {

        fetchUsers();

    }, []);


    const handleCreate = async (e) => {

        e.preventDefault();

        try {

            setCreating(true);
            setError("");

            await createAdminUser({
                username: username.trim(),
                password,
                role,
            });

            setUsername("");
            setPassword("");
            setRole("OPERATOR");

            setShowCreateForm(false);

            await fetchUsers();

        } catch (err) {

            console.error(
                "Admin kullanıcı oluşturulamadı:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Kullanıcı oluşturulurken bir hata oluştu."
            );

        } finally {

            setCreating(false);

        }
    };


    const handleDeactivate = async (id) => {

        const confirmed = window.confirm(
            "Bu kullanıcıyı pasifleştirmek istediğinize emin misiniz?"
        );

        if (!confirmed) {
            return;
        }

        try {

            setError("");

            await deactivateAdminUser(id);

            await fetchUsers();

        } catch (err) {

            console.error(
                "Kullanıcı pasifleştirilemedi:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Kullanıcı pasifleştirilirken bir hata oluştu."
            );

        }
    };


    const getRoleLabel = (role) => {

        switch (role) {

            case "SUPER_ADMIN":
                return "Super Admin";

            case "OPERATOR":
                return "Operatör";

            case "VIEWER":
                return "Görüntüleyici";

            default:
                return role || "-";
        }
    };


    const getRoleClass = (role) => {

        switch (role) {

            case "SUPER_ADMIN":
                return "bg-purple-100 text-purple-700";

            case "OPERATOR":
                return "bg-blue-100 text-blue-700";

            case "VIEWER":
                return "bg-gray-100 text-gray-700";

            default:
                return "bg-gray-100 text-gray-600";
        }
    };


    const formatDate = (date) => {

        if (!date) {
            return "-";
        }

        return new Date(date).toLocaleString(
            "tr-TR"
        );
    };


    if (loading) {

        return (
            <div className="p-8">

                <p className="text-gray-500">
                    Kullanıcılar yükleniyor...
                </p>

            </div>
        );
    }


    return (

        <div className="p-8">

            {/* HEADER */}

            <div className="flex items-center justify-between mb-6">

                <div>

                    <h1 className="text-3xl font-bold text-gray-900">
                        Admin Kullanıcıları
                    </h1>

                    <p className="mt-1 text-gray-500">
                        Sistem yöneticilerini ve yetkilerini yönetin.
                    </p>

                </div>


                <button
                    onClick={() =>
                        setShowCreateForm(!showCreateForm)
                    }
                    className="
                        bg-gray-900
                        text-white
                        px-5
                        py-2
                        rounded-lg
                        hover:bg-gray-800
                    "
                >
                    {showCreateForm
                        ? "Formu Kapat"
                        : "Yeni Kullanıcı"}
                </button>

            </div>


            {/* ERROR */}

            {error && (

                <div className="
                    mb-6
                    border
                    border-red-200
                    bg-red-50
                    text-red-700
                    rounded-lg
                    px-4
                    py-3
                ">
                    {error}
                </div>

            )}


            {/* CREATE FORM */}

            {showCreateForm && (

                <form
                    onSubmit={handleCreate}
                    className="
                        bg-white
                        border
                        rounded-xl
                        p-5
                        mb-6
                    "
                >

                    <h2 className="
                        text-lg
                        font-semibold
                        text-gray-900
                        mb-4
                    ">
                        Yeni Admin Kullanıcısı
                    </h2>


                    <div className="
                        grid
                        grid-cols-1
                        md:grid-cols-3
                        gap-4
                    ">

                        {/* USERNAME */}

                        <div>

                            <label className="
                                block
                                text-sm
                                font-medium
                                text-gray-700
                                mb-1
                            ">
                                Kullanıcı Adı
                            </label>

                            <input
                                type="text"
                                value={username}
                                onChange={(e) =>
                                    setUsername(e.target.value)
                                }
                                minLength={3}
                                maxLength={50}
                                required
                                placeholder="Kullanıcı adı"
                                className="
                                    w-full
                                    border
                                    rounded-lg
                                    px-3
                                    py-2
                                "
                            />

                        </div>


                        {/* PASSWORD */}

                        <div>

                            <label className="
                                block
                                text-sm
                                font-medium
                                text-gray-700
                                mb-1
                            ">
                                Şifre
                            </label>

                            <input
                                type="password"
                                value={password}
                                onChange={(e) =>
                                    setPassword(e.target.value)
                                }
                                minLength={8}
                                required
                                placeholder="En az 8 karakter"
                                className="
                                    w-full
                                    border
                                    rounded-lg
                                    px-3
                                    py-2
                                "
                            />

                        </div>


                        {/* ROLE */}

                        <div>

                            <label className="
                                block
                                text-sm
                                font-medium
                                text-gray-700
                                mb-1
                            ">
                                Rol
                            </label>

                            <select
                                value={role}
                                onChange={(e) =>
                                    setRole(e.target.value)
                                }
                                className="
                                    w-full
                                    border
                                    rounded-lg
                                    px-3
                                    py-2
                                    bg-white
                                "
                            >

                                <option value="OPERATOR">
                                    Operatör
                                </option>

                                <option value="VIEWER">
                                    Görüntüleyici
                                </option>

                                <option value="SUPER_ADMIN">
                                    Super Admin
                                </option>

                            </select>

                        </div>

                    </div>


                    {/* CREATE BUTTON */}

                    <div className="flex justify-end mt-5">

                        <button
                            type="submit"
                            disabled={creating}
                            className="
                                bg-gray-900
                                text-white
                                px-5
                                py-2
                                rounded-lg
                                hover:bg-gray-800
                                disabled:opacity-50
                            "
                        >
                            {creating
                                ? "Oluşturuluyor..."
                                : "Kullanıcı Oluştur"}
                        </button>

                    </div>

                </form>

            )}


            {/* TABLE */}

            <div className="
                bg-white
                border
                rounded-xl
                overflow-hidden
            ">

                <div className="overflow-x-auto">

                    <table className="w-full">

                        <thead className="
                            bg-gray-50
                            border-b
                        ">

                            <tr>

                                <th className="text-left px-5 py-4">
                                    Kullanıcı Adı
                                </th>

                                <th className="text-left px-5 py-4">
                                    Rol
                                </th>

                                <th className="text-left px-5 py-4">
                                    Durum
                                </th>

                                <th className="text-left px-5 py-4">
                                    Son Giriş
                                </th>

                                <th className="text-left px-5 py-4">
                                    İşlem
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                            {users.length === 0 ? (

                                <tr>

                                    <td
                                        colSpan={5}
                                        className="
                                            text-center
                                            py-10
                                            text-gray-500
                                        "
                                    >
                                        Kayıtlı admin kullanıcısı bulunamadı.
                                    </td>

                                </tr>

                            ) : (

                                users.map((user) => (

                                    <tr
                                        key={user.id}
                                        className="
                                            border-b
                                            last:border-b-0
                                            hover:bg-gray-50
                                        "
                                    >

                                        {/* USERNAME */}

                                        <td className="
                                            px-5
                                            py-4
                                            font-medium
                                            text-gray-900
                                        ">
                                            {user.username}
                                        </td>


                                        {/* ROLE */}

                                        <td className="px-5 py-4">

                                            <span className={`
                                                inline-flex
                                                px-3
                                                py-1
                                                rounded-full
                                                text-xs
                                                font-medium
                                                ${getRoleClass(user.role)}
                                            `}>
                                                {getRoleLabel(user.role)}
                                            </span>

                                        </td>


                                        {/* STATUS */}

                                        <td className="px-5 py-4">

                                            <span className={`
                                                inline-flex
                                                px-3
                                                py-1
                                                rounded-full
                                                text-xs
                                                font-medium
                                                ${
                                                    user.active
                                                        ? "bg-green-100 text-green-700"
                                                        : "bg-red-100 text-red-700"
                                                }
                                            `}>
                                                {user.active
                                                    ? "Aktif"
                                                    : "Pasif"}
                                            </span>

                                        </td>


                                        {/* LAST LOGIN */}

                                        <td className="
                                            px-5
                                            py-4
                                            text-sm
                                            text-gray-600
                                        ">
                                            {formatDate(
                                                user.lastLoginAt
                                            )}
                                        </td>


                                        {/* ACTION */}

                                        <td className="px-5 py-4">

                                            {user.active ? (

                                                <button
                                                    onClick={() =>
                                                        handleDeactivate(
                                                            user.id
                                                        )
                                                    }
                                                    className="
                                                        border
                                                        border-red-200
                                                        text-red-600
                                                        px-3
                                                        py-1.5
                                                        rounded-lg
                                                        text-sm
                                                        hover:bg-red-50
                                                    "
                                                >
                                                    Pasifleştir
                                                </button>

                                            ) : (

                                                <span className="
                                                    text-sm
                                                    text-gray-400
                                                ">
                                                    Pasif
                                                </span>

                                            )}

                                        </td>

                                    </tr>

                                ))

                            )}

                        </tbody>

                    </table>

                </div>

            </div>

        </div>
    );
}

export default Users;